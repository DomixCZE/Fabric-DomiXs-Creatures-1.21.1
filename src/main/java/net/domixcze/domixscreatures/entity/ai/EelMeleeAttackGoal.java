package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.custom.EelEntity;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Box;

import java.util.List;

public class EelMeleeAttackGoal extends MeleeAttackGoal {
    private final EelEntity eel;
    private static final int ATTACK_RADIUS = 3;
    private static final int ATTACK_TIME = 40;
    public static final int COOLDOWN_TIME = EelEntity.ATTACK_COOLDOWN;
    private int attackDuration = 0;

    public EelMeleeAttackGoal(EelEntity eel, double speed, boolean pauseWhenMobIdle) {
        super(eel, speed, pauseWhenMobIdle);
        this.eel = eel;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.eel.getTarget();
        boolean isTargetInWater = target != null && target.isTouchingWater() && isTargetInRange();

        return this.eel.isCharged() && this.eel.isTouchingWater() && (isTargetInWater);
    }

    private boolean isTargetInRange() {
        LivingEntity target = eel.getTarget();
        return target != null && eel.squaredDistanceTo(target) <= ATTACK_RADIUS;
    }

    @Override
    public boolean shouldContinue() {
        return attackDuration > 0;
    }

    @Override
    public void start() {
        attackDuration = ATTACK_TIME;
        this.eel.setCharged(false);
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.playSoundFromEntity(null, this.eel, ModSounds.EEL_ATTACK, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }

    @Override
    public void stop() {
        this.eel.setAttackCooldown(COOLDOWN_TIME);
        attackDuration = 0;
    }

    @Override
    public void tick() {
        if (attackDuration > 0) {
            attackDuration--;
            spawnParticles();
            applyEffectInRadius();
        }
    }

    private void spawnParticles() {
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            double centerX = this.eel.getX();
            double centerY = this.eel.getY() + 0.5;
            double centerZ = this.eel.getZ();

            double pulseRadius = (double) ATTACK_RADIUS * ((float) (ATTACK_TIME - attackDuration) / ATTACK_TIME);

            for (double angle = 0; angle < 360; angle += 10) {
                double radians = Math.toRadians(angle);
                double xOffset = Math.cos(radians) * pulseRadius;
                double zOffset = Math.sin(radians) * pulseRadius;

                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK, centerX + xOffset, centerY, centerZ + zOffset, 1, 0, 0, 0, 0.1);
            }
        }
    }

    private void applyEffectInRadius() {
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            double centerX = this.eel.getX();
            double centerZ = this.eel.getZ();
            double centerY = this.eel.getY();
            int blockY = (int) Math.floor(centerY);

            double pulseRadius = (double) ATTACK_RADIUS * ((float) (ATTACK_TIME - attackDuration) / ATTACK_TIME);

            Box area = new Box(centerX - pulseRadius, blockY, centerZ - pulseRadius,
                    centerX + pulseRadius, blockY + 1, centerZ + pulseRadius);

            List<LivingEntity> entities = serverWorld.getEntitiesByClass(LivingEntity.class, area, entity -> entity != eel);
            for (LivingEntity entity : entities) {
                entity.addStatusEffect(new StatusEffectInstance(ModEffects.ELECTRIFIED, 200, 0));
            }
        }
    }
}
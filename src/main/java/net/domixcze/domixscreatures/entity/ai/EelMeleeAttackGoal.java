package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.custom.EelEntity;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EelMeleeAttackGoal extends MeleeAttackGoal {
    private final EelEntity eel;
    private static final int ATTACK_RADIUS = 3;
    private static final int ATTACK_TIME = 40;
    public static final int COOLDOWN_TIME = EelEntity.ATTACK_COOLDOWN;
    private int attackDuration = 0;

    private final Set<BlockPos> poweredBlocks = new HashSet<>();

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
        return target != null && eel.squaredDistanceTo(target) <= (ATTACK_RADIUS * ATTACK_RADIUS);
    }

    @Override
    public boolean shouldContinue() {
        return attackDuration > 0;
    }

    @Override
    public void start() {
        attackDuration = ATTACK_TIME;
        poweredBlocks.clear();
        this.eel.setCharged(false);
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.playSoundFromEntity(null, this.eel, ModSounds.EEL_ATTACK, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }
    }

    @Override
    public void stop() {
        this.eel.setAttackCooldown(COOLDOWN_TIME);
        attackDuration = 0;
        poweredBlocks.clear();
    }

    @Override
    public void tick() {
        if (attackDuration > 0) {
            attackDuration--;
            spawnParticles();
            applyEffectInRadius();
            triggerRedstone();
        }
    }

    private void triggerRedstone() {
        World world = this.eel.getWorld();
        BlockPos centerPos = this.eel.getBlockPos();

        double pulseRadius = (double) ATTACK_RADIUS * ((float) (ATTACK_TIME - attackDuration) / ATTACK_TIME);
        double pulseRadiusSq = (pulseRadius + 0.5) * (pulseRadius + 0.5);

        int r = ATTACK_RADIUS;
        for (BlockPos pos : BlockPos.iterate(centerPos.add(-r, -r, -r), centerPos.add(r, r, r))) {
            if (poweredBlocks.contains(pos)) continue;

            double dx = pos.getX() + 0.5 - this.eel.getX();
            double dy = pos.getY() + 0.5 - this.eel.getY();
            double dz = pos.getZ() + 0.5 - this.eel.getZ();
            double distanceSq = dx * dx + dy * dy + dz * dz;

            if (distanceSq <= pulseRadiusSq) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof LightningRodBlock rod) {
                    rod.setPowered(state, world, pos);
                    poweredBlocks.add(pos.toImmutable());

                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                3, 0.1, 0.1, 0.1, 0.02);
                    }
                }
            }
        }
    }

    private void spawnParticles() {
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            double centerX = this.eel.getX();
            double centerY = this.eel.getY() + 0.5;
            double centerZ = this.eel.getZ();

            double pulseRadius = (double) ATTACK_RADIUS * ((float) (ATTACK_TIME - attackDuration) / ATTACK_TIME);

            for (double angle = 0; angle < 360; angle += 15) {
                double radians = Math.toRadians(angle);
                double xOffset = Math.cos(radians) * pulseRadius;
                double zOffset = Math.sin(radians) * pulseRadius;

                serverWorld.spawnParticles(ModParticles.ELECTRIC, centerX + xOffset, centerY, centerZ + zOffset, 1, 0, 0, 0, 0.1);
            }
        }
    }

    private void applyEffectInRadius() {
        if (this.eel.getWorld() instanceof ServerWorld serverWorld) {
            double pulseRadius = (double) ATTACK_RADIUS * ((float) (ATTACK_TIME - attackDuration) / ATTACK_TIME);

            Box area = this.eel.getBoundingBox().expand(pulseRadius);
            List<LivingEntity> entities = serverWorld.getEntitiesByClass(LivingEntity.class, area,
                    entity -> entity != eel && eel.squaredDistanceTo(entity) <= (pulseRadius * pulseRadius));

            for (LivingEntity entity : entities) {
                entity.addStatusEffect(new StatusEffectInstance(ModEffects.ELECTRIFIED, 200, 0));
            }
        }
    }
}
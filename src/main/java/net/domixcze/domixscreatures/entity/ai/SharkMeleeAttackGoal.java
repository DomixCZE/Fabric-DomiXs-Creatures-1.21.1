package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.custom.SharkEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class SharkMeleeAttackGoal extends MeleeAttackGoal {
    private final SharkEntity shark;
    private final double attackRange;
    private final double detectionRange = 20.0;

    public SharkMeleeAttackGoal(SharkEntity shark, double speed, boolean pauseWhenMobIdle, double attackRange) {
        super(shark, speed, pauseWhenMobIdle);
        this.shark = shark;
        this.attackRange = attackRange * attackRange;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.shark.getTarget();

        if (target != null && target.isTouchingWater()) {
            return true;
        }

        List<PlayerEntity> nearbyPlayers = this.shark.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                this.shark.getBoundingBox().expand(detectionRange),
                player -> player.isAlive() && !player.isSpectator() && !player.getAbilities().creativeMode
        );

        for (PlayerEntity player : nearbyPlayers) {
            StatusEffectInstance effect = player.getStatusEffect(ModEffects.BLEEDING);
            if (effect != null) {
                this.shark.setTarget(player);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.shark.getTarget();
        if (target != null && target.isOnGround() && !target.isTouchingWater()) {
            return false;
        }
        return super.shouldContinue();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return this.attackRange;
    }

    @Override
    protected void attack(LivingEntity target, double squaredDistance) {
        super.attack(target, squaredDistance);
    }
}
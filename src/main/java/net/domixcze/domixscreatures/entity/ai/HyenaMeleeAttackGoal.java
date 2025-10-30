package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.HyenaEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HyenaMeleeAttackGoal extends ModMeleeAttackGoal<HyenaEntity> {
    private final HyenaEntity hyena;

    public HyenaMeleeAttackGoal(HyenaEntity hyena, double speed, boolean pauseWhenMobIdle) {
        super(hyena, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.hyena = hyena;
    }

    @Override
    public boolean canStart() {
        if (this.hyena.isBaby() || this.hyena.isSleeping()) {
            return false;
        }

        LivingEntity target = this.hyena.getTarget();
        if (target != null) {
            if (target instanceof PlayerEntity
                    || target instanceof IllagerEntity
                    || target instanceof VillagerEntity) {
                if (target.getHealth() > target.getMaxHealth() / 2f) {
                    return false;
                }
            }
        }

        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.hyena.getTarget();
        if (target != null) {
            if (target instanceof PlayerEntity
                    || target instanceof IllagerEntity
                    || target instanceof VillagerEntity) {
                if (target.getHealth() > target.getMaxHealth() / 2f) {
                    return false;
                }
            }
        }

        return super.shouldContinue();
    }
}
package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.TigerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.TameableEntity;

public class TigerMeleeAttackGoal extends MeleeAttackGoal {
    private final TigerEntity tiger;
    private final double attackRange;

    public TigerMeleeAttackGoal(TigerEntity tiger, double speed, boolean pauseWhenMobIdle, double attackRange) {
        super(tiger, speed, pauseWhenMobIdle);
        this.tiger = tiger;
        this.attackRange = attackRange * attackRange;
    }

    @Override
    public boolean canStart() {
        if (this.tiger.isBaby()) {
            return false;
        }
        if (this.tiger.isSleeping()) {
            return false;
        }

        LivingEntity target = this.tiger.getTarget();
        if (target instanceof TameableEntity tameableTarget) {
            if (tameableTarget.getOwnerUuid() != null && this.tiger.getOwnerUuid() != null && tameableTarget.getOwnerUuid().equals(this.tiger.getOwnerUuid())) {
                return false;
            }
        }
        return super.canStart();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return this.attackRange;
    }
}
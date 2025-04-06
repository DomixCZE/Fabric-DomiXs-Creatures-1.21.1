package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class HippoMeleeAttackGoal extends MeleeAttackGoal {
    private final HippoEntity hippo;
    private final double attackRange;

    public HippoMeleeAttackGoal(HippoEntity hippo, double speed, boolean pauseWhenMobIdle, double attackRange) {
        super(hippo, speed, pauseWhenMobIdle);
        this.hippo = hippo;
        this.attackRange = attackRange * attackRange;
    }

    @Override
    public boolean canStart() {
        if (this.hippo.isBaby()) {
            return false;
        }
        if (this.hippo.isSleeping()) {
            return false;
        }
        return super.canStart();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return this.attackRange;
    }
}
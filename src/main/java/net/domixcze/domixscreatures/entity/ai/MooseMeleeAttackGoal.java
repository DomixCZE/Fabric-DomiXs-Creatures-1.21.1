package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.MooseEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class MooseMeleeAttackGoal extends MeleeAttackGoal {
    private final MooseEntity moose;

    public MooseMeleeAttackGoal(MooseEntity moose, double speed, boolean pauseWhenMobIdle) {
        super(moose, speed, pauseWhenMobIdle);
        this.moose = moose;
    }

    @Override
    public boolean canStart() {
        if (this.moose.isBaby()) {
            return false;
        }
        if (this.moose.isSleeping()) {
            return false;
        }

        return super.canStart();
    }
}
package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.MooseEntity;

public class MooseMeleeAttackGoal extends ModMeleeAttackGoal<MooseEntity> {
    private final MooseEntity moose;

    public MooseMeleeAttackGoal(MooseEntity moose, double speed, boolean pauseWhenMobIdle) {
        super(moose, speed, pauseWhenMobIdle, 12, "land_controller", null);
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
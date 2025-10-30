package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.BoarEntity;

public class BoarMeleeAttackGoal extends ModMeleeAttackGoal<BoarEntity> {
    private final BoarEntity boar;

    public BoarMeleeAttackGoal(BoarEntity boar, double speed, boolean pauseWhenMobIdle) {
        super(boar, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.boar = boar;
    }

    @Override
    public boolean canStart() {
        if (boar.isBaby()) {
            return false;
        }
        if (boar.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
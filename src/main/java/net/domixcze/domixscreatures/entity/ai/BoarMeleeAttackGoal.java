package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.BoarEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class BoarMeleeAttackGoal extends MeleeAttackGoal {
    private final BoarEntity boar;

    public BoarMeleeAttackGoal(BoarEntity boar, double speed, boolean pauseWhenMobIdle) {
        super(boar, speed, pauseWhenMobIdle);
        this.boar = boar;
    }

    @Override
    public boolean canStart() {
        if (this.boar.isBaby()) {
            return false;
        }
        if (this.boar.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.GorillaEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class GorillaMeleeAttackGoal extends MeleeAttackGoal {
    private final GorillaEntity gorilla;

    public GorillaMeleeAttackGoal(GorillaEntity gorilla, double speed, boolean pauseWhenMobIdle) {
        super(gorilla, speed, pauseWhenMobIdle);
        this.gorilla = gorilla;
    }

    @Override
    public boolean canStart() {
        if (gorilla.isBaby()) {
            return false;
        }
        if (gorilla.isSleeping()) {
            return false;
        }
        if (gorilla.isChestBanging()) {
            return false;
        }
        if (gorilla.isEating()) {
            return false;
        }
        if (gorilla.isSitting()) {
            return false;
        }
        return super.canStart();
    }
}
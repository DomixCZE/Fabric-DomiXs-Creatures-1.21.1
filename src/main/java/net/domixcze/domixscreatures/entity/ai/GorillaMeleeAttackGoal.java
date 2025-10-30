package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.GorillaEntity;

public class GorillaMeleeAttackGoal extends ModMeleeAttackGoal<GorillaEntity> {
    private final GorillaEntity gorilla;

    public GorillaMeleeAttackGoal(GorillaEntity gorilla, double speed, boolean pauseWhenMobIdle) {
        super(gorilla, speed, pauseWhenMobIdle, 10, "land_controller", null);
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
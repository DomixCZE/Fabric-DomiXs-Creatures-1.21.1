package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.SunBearEntity;

public class SunBearMeleeAttackGoal extends ModMeleeAttackGoal<SunBearEntity> {
    private final SunBearEntity bear;

    public SunBearMeleeAttackGoal(SunBearEntity bear, double speed, boolean pauseWhenMobIdle) {
        super(bear, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.bear = bear;
    }

    @Override
    public boolean canStart() {
        if (this.bear.isBaby()) {
            return false;
        }
        if (this.bear.isSleeping()) {
            return false;
        }
        if (this.bear.isHoneyed()) {
            return false;
        }
        return super.canStart();
    }
}
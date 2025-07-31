package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.SunBearEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class SunBearMeleeAttackGoal extends MeleeAttackGoal {
    private final SunBearEntity bear;

    public SunBearMeleeAttackGoal(SunBearEntity bear, double speed, boolean pauseWhenMobIdle) {
        super(bear, speed, pauseWhenMobIdle);
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
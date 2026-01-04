package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.RatEntity;

public class RatMeleeAttackGoal extends ModMeleeAttackGoal<RatEntity> {
    private final RatEntity rat;

    public RatMeleeAttackGoal(RatEntity rat, double speed, boolean pauseWhenIdle) {
        super(rat, speed, pauseWhenIdle, 10, "land_controller", null);
        this.rat = rat;
    }

    @Override
    public boolean canStart() {
        if (rat.isBaby()) {
            return false;
        }
        if (rat.isSleeping()) {
            return false;
        }

        return super.canStart();
    }
}
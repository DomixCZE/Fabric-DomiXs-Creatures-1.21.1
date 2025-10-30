package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.HippoEntity;

public class HippoMeleeAttackGoal extends ModMeleeAttackGoal<HippoEntity> {
    private final HippoEntity hippo;

    public HippoMeleeAttackGoal(HippoEntity hippo, double speed, boolean pauseWhenMobIdle) {
        super(hippo, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.hippo = hippo;
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
}
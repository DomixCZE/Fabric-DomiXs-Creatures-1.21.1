package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.CheetahEntity;

public class CheetahMeleeAttackGoal extends ModMeleeAttackGoal<CheetahEntity> {
    private final CheetahEntity cheetah;

    public CheetahMeleeAttackGoal(CheetahEntity cheetah, double speed, boolean pauseWhenMobIdle) {
        super(cheetah, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.cheetah = cheetah;
    }

    @Override
    public boolean canStart() {
        if (cheetah.isBaby()) {
            return false;
        }
        if (cheetah.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
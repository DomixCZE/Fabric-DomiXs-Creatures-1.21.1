package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class HippoMeleeAttackGoal extends MeleeAttackGoal {
    private final HippoEntity hippo;

    public HippoMeleeAttackGoal(HippoEntity hippo, double speed, boolean pauseWhenMobIdle) {
        super(hippo, speed, pauseWhenMobIdle);
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
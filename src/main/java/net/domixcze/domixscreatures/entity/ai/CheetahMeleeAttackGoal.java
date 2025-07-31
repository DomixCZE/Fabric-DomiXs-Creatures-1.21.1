package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.CheetahEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class CheetahMeleeAttackGoal extends MeleeAttackGoal {
    private final CheetahEntity cheetah;

    public CheetahMeleeAttackGoal(CheetahEntity cheetah, double speed, boolean pauseWhenMobIdle) {
        super(cheetah, speed, pauseWhenMobIdle);
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
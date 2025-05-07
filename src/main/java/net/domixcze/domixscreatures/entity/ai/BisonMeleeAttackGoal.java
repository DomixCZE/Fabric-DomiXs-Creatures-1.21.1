package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.BisonEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class BisonMeleeAttackGoal extends MeleeAttackGoal {
    private final BisonEntity bison;

    public BisonMeleeAttackGoal(BisonEntity bison, double speed, boolean pauseWhenMobIdle) {
        super(bison, speed, pauseWhenMobIdle);
        this.bison = bison;
    }

    @Override
    public boolean canStart() {
        if (this.bison.isBaby()) {
            return false;
        }
        if (this.bison.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
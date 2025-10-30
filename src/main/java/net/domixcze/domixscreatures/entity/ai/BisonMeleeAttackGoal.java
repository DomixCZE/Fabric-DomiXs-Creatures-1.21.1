package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.BisonEntity;

public class BisonMeleeAttackGoal extends ModMeleeAttackGoal<BisonEntity> {
    private final BisonEntity bison;

    public BisonMeleeAttackGoal(BisonEntity bison, double speed, boolean pauseWhenMobIdle) {
        super(bison, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.bison = bison;
    }

    @Override
    public boolean canStart() {
        if (bison.isBaby()) {
            return false;
        }
        if (bison.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
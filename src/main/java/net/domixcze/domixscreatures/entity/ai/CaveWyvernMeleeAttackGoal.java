package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;

public class CaveWyvernMeleeAttackGoal extends ModMeleeAttackGoal<CaveWyvernEntity> {
    private final CaveWyvernEntity wyvern;

    public CaveWyvernMeleeAttackGoal(CaveWyvernEntity wyvern, double speed, boolean pauseWhenMobIdle) {
        super(wyvern, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.wyvern = wyvern;
    }

    @Override
    public boolean canStart() {
        if (this.wyvern.isPale() && this.wyvern.isExposedToSun()) return false;

        if (wyvern.isBaby()) return false;
        if (wyvern.isSleeping()) return false;
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.wyvern.isPale() && this.wyvern.isExposedToSun()) return false;
        return super.shouldContinue();
    }
}
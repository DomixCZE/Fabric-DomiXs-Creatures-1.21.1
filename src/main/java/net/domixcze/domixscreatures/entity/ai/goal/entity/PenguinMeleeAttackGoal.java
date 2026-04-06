package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.custom.PenguinEntity;

public class PenguinMeleeAttackGoal extends ModMeleeAttackGoal<PenguinEntity> {
    private final PenguinEntity penguin;

    public PenguinMeleeAttackGoal(PenguinEntity penguin, double speed, boolean pauseWhenMobIdle) {
        super(penguin, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.penguin = penguin;
    }

    @Override
    public boolean canStart() {
        if (penguin.isBaby()) {
            return false;
        }
        if (penguin.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
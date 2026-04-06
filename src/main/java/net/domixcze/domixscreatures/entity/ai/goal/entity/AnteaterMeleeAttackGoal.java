package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.custom.AnteaterEntity;

public class AnteaterMeleeAttackGoal extends ModMeleeAttackGoal<AnteaterEntity> {
    private final AnteaterEntity anteater;

    public AnteaterMeleeAttackGoal(AnteaterEntity anteater, double speed, boolean pauseWhenMobIdle) {
        super(anteater, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.anteater = anteater;
    }

    @Override
    public boolean canStart() {
        if (anteater.getHealth() <= anteater.getMaxHealth() / 2f) {
            return false;
        }
        return super.canStart();
    }
}
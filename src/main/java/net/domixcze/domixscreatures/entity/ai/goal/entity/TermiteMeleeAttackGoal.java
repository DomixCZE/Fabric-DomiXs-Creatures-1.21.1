package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.client.termite.TermiteVariants;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;

public class TermiteMeleeAttackGoal extends ModMeleeAttackGoal<TermiteEntity> {
    private final TermiteEntity termite;

    public TermiteMeleeAttackGoal(TermiteEntity termite, double speed, boolean pauseWhenMobIdle) {
        super(termite, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.termite = termite;
    }

    @Override
    public boolean canStart() {
        if (termite.getVariant() != TermiteVariants.SOLDIER) {
            return false;
        }
        return super.canStart();
    }
}
package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.custom.KomodoDragonEntity;

public class KomodoDragonMeleeAttackGoal extends ModMeleeAttackGoal<KomodoDragonEntity> {
    private final KomodoDragonEntity komodo;

    public KomodoDragonMeleeAttackGoal(KomodoDragonEntity komodo, double speed, boolean pauseWhenMobIdle) {
        super(komodo, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.komodo = komodo;
    }

    @Override
    public boolean canStart() {
        if (this.komodo.isBaby()) {
            return false;
        }
        if (this.komodo.isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}
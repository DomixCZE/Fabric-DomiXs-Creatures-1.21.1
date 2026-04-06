package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.client.ant.AntVariants;
import net.domixcze.domixscreatures.entity.custom.AntEntity;

public class AntMeleeAttackGoal extends ModMeleeAttackGoal<AntEntity> {
    private final AntEntity ant;

    public AntMeleeAttackGoal(AntEntity ant, double speed, boolean pauseWhenMobIdle) {
        super(ant, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.ant = ant;
    }

    @Override
    public boolean canStart() {
        if (ant.getVariant() != AntVariants.SOLDIER) {
            return false;
        }
        return super.canStart();
    }
}
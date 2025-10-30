package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;

public class FireSalamanderMeleeAttackGoal extends ModMeleeAttackGoal<FireSalamanderEntity> {
    private final FireSalamanderEntity salamander;

    public FireSalamanderMeleeAttackGoal(FireSalamanderEntity salamander, double speed, boolean pauseWhenMobIdle) {
        super(salamander, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.salamander = salamander;
    }

    @Override
    public boolean canStart() {
        if (this.salamander.isObsidianVariant()) {
            return false;
        }
        return super.canStart();
    }
}
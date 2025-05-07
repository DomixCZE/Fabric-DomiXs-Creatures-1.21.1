package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.MoleEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class MoleMeleeAttackGoal extends MeleeAttackGoal {
    private final MoleEntity mole;

    public MoleMeleeAttackGoal(MoleEntity mole, double speed, boolean pauseWhenMobIdle) {
        super(mole, speed, pauseWhenMobIdle);
        this.mole = mole;
    }

    @Override
    public boolean canStart() {
        if (this.mole.isBaby()) {
            return false;
        }

        return super.canStart();
    }
}
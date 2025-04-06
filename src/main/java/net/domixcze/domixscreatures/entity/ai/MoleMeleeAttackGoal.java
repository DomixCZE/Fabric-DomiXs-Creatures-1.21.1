package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.MoleEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class MoleMeleeAttackGoal extends MeleeAttackGoal {
    private final MoleEntity mole;
    private final double attackRange;

    public MoleMeleeAttackGoal(MoleEntity mole, double speed, boolean pauseWhenMobIdle, double attackRange) {
        super(mole, speed, pauseWhenMobIdle);
        this.mole = mole;
        this.attackRange = attackRange * attackRange;
    }

    @Override
    public boolean canStart() {
        if (this.mole.isBaby()) {
            return false;
        }

        return super.canStart();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return this.attackRange;
    }
}
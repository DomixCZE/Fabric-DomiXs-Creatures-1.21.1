package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class FireSalamanderMeleeAttackGoal extends MeleeAttackGoal {

    private final FireSalamanderEntity salamander;
    private final double attackRange;

    public FireSalamanderMeleeAttackGoal(FireSalamanderEntity salamander, double speed, boolean pauseWhenMobIdle, double attackRange) {
        super(salamander, speed, pauseWhenMobIdle);
        this.salamander = salamander;
        this.attackRange = attackRange * attackRange;
    }

    @Override
    public boolean canStart() {
        if (this.salamander.isObsidianVariant()) {
            return false;
        }
        return super.canStart();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity target) {
        return this.attackRange;
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.salamander.getTarget();
        if (target != null && this.salamander.isAlive() && this.salamander.squaredDistanceTo(target) <= this.attackRange) {
            if (this.salamander.handSwinging) {
                target.setOnFireFor(5);
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

public class FireSalamanderMeleeAttackGoal extends MeleeAttackGoal {

    private final FireSalamanderEntity salamander;

    public FireSalamanderMeleeAttackGoal(FireSalamanderEntity salamander, double speed, boolean pauseWhenMobIdle) {
        super(salamander, speed, pauseWhenMobIdle);
        this.salamander = salamander;
    }

    @Override
    public boolean canStart() {
        if (this.salamander.isObsidianVariant()) {
            return false;
        }

        return super.canStart();
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.salamander.getTarget();
        if (target != null && this.salamander.isAlive() && this.salamander.isInAttackRange(target)) {
            if (this.salamander.handSwinging) {
                target.setOnFireFor(5);
            }
        }
    }
}
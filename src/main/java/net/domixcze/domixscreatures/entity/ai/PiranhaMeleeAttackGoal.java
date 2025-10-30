package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.PiranhaEntity;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class PiranhaMeleeAttackGoal extends ModMeleeAttackGoal<PiranhaEntity> {
    private final PiranhaEntity piranha;

    public PiranhaMeleeAttackGoal(PiranhaEntity piranha, double speed, boolean pauseWhenMobIdle) {
        super(piranha, speed, pauseWhenMobIdle, 10, "controller", null);
        this.piranha = piranha;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = piranha.getTarget();
        if (target != null && target.isAlive() && target.isTouchingWater() && piranha.isTouchingWater()) {
            return super.canStart();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = piranha.getTarget();
        if (target != null && target.isAlive() && target.isTouchingWater() && piranha.isTouchingWater()) {
            return super.shouldContinue();
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        double radius = 8.0;
        List<PiranhaEntity> nearby = piranha.getWorld().getEntitiesByClass(
                PiranhaEntity.class,
                piranha.getBoundingBox().expand(radius),
                p -> p != piranha && p.isAlive() && p.getTarget() == null && p.isTouchingWater()
        );

        for (PiranhaEntity ally : nearby) {
            ally.setTarget(piranha.getTarget());
        }
    }
}
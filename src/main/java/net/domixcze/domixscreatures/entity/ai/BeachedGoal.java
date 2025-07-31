package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class BeachedGoal extends Goal {
    private final Beachable beachableEntity;
    private final MobEntity entity;

    public BeachedGoal(Beachable beachableEntity, MobEntity entity) {
        this.beachableEntity = beachableEntity;
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return entity.isOnGround() && !entity.isTouchingWater();
    }

    @Override
    public void start() {
        beachableEntity.setBeached(true);
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        return canStart();
    }

    @Override
    public void stop() {
        beachableEntity.setBeached(false);
    }

    @Override
    public void tick() {
        if (beachableEntity.isBeached()) {
            this.entity.getNavigation().stop();
            this.entity.getLookControl().tick();
        } else {
            super.tick();
        }
    }
}

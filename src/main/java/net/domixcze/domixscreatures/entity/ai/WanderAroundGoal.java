package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class WanderAroundGoal extends WanderAroundFarGoal {
    public WanderAroundGoal(PathAwareEntity pathAwareEntity, double d) {
        super(pathAwareEntity, d);
    }

    @Override
    public boolean canStart() {
        if (this.mob instanceof Sleepy && ((Sleepy) this.mob).isSleeping()) {
            return false;
        }
        return super.canStart();
    }
}

package net.domixcze.domixscreatures.entity.ai.goal;

import net.domixcze.domixscreatures.entity.ai.goal.util.Baskable;
import net.domixcze.domixscreatures.entity.ai.goal.util.Sleepy;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

public class MoveToSunGoal<T extends PathAwareEntity & Baskable> extends MoveToBlockGoal {
    private final T mob;
    private final int startTime;
    private final int endTime;

    public MoveToSunGoal(T mob, int searchRadius, int verticalRange, int startTime, int endTime) {
        super(mob, (state, pos) ->
                        state.isSideSolidFullSquare(mob.getWorld(), pos, Direction.UP) &&
                                mob.getWorld().getLightLevel(LightType.SKY, pos.up()) >= 15,
                searchRadius, verticalRange);

        this.mob = mob;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean canStart() {
        if (mob instanceof Sleepy sleepy && sleepy.isSleeping()) return false;
        if (mob instanceof TameableEntity tameable && tameable.isSitting()) return false;
        if (mob.hasVehicle() || mob.hasPassengers()) return false;
        if (mob.getAttacker() != null || mob.getTarget() != null) return false;

        long time = mob.getWorld().getTimeOfDay() % 24000;
        boolean isBaskingTime = time >= startTime && time <= endTime && mob.getWorld().isDay() && !mob.getWorld().isRaining();

        if (mob.isBasking() || mob.getBaskingCooldown() > 0 || !isBaskingTime) return false;

        return super.canStart();
    }

    @Override
    protected void onReachedTarget(BlockPos pos) {
        this.mob.setBasking(true);
        this.mob.getNavigation().stop();
    }
}

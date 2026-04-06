package net.domixcze.domixscreatures.entity.ai.brain.task.bird;

import net.domixcze.domixscreatures.entity.ai.brain.util.Bird;
import net.domixcze.domixscreatures.entity.ai.brain.util.TameableBird;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Map;

public class TakeoffFlightTask<T extends PathAwareEntity & Bird> extends MultiTickTask<T> {

    public TakeoffFlightTask() {
        super(Map.of(
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.BREED_TARGET, MemoryModuleState.REGISTERED
        ));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, T entity) {
        if (!entity.canStartFlying()) {
            return false;
        }

        if (entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).isPresent()) {
            return false;
        }

        if (this.hasNearbyThreat(world, entity)) {
            return true;
        }

        return this.shouldRandomlyTakeOff(entity);
    }

    @Override
    protected void run(ServerWorld world, T entity, long time) {
        entity.startFlying();

        double horizontalOffset = entity.getTakeoffHorizontalOffset();
        Vec3d takeoffTarget = entity.getPos().add(
                (entity.getRandom().nextDouble() - 0.5D) * horizontalOffset,
                entity.getTakeoffMinY() + entity.getRandom().nextDouble() * entity.getTakeoffExtraY(),
                (entity.getRandom().nextDouble() - 0.5D) * horizontalOffset
        );

        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(takeoffTarget));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(takeoffTarget, entity.getTakeoffSpeed(), 0));
    }

    private boolean shouldRandomlyTakeOff(T entity) {
        if (!entity.isOnGround()) {
            return false;
        }

        if (this.shouldSuppressRandomTakeoff(entity)) {
            return false;
        }

        return entity.getRandom().nextInt(entity.getRandomTakeoffInterval()) == 0;
    }

    private boolean shouldSuppressRandomTakeoff(T entity) {
        if (!(entity instanceof TameableBird tameableBird)) {
            return false;
        }

        if (!tameableBird.isTamed() || tameableBird.isSitting() || entity.hasVehicle()) {
            return false;
        }

        LivingEntity owner = tameableBird.getOwner();
        if (owner == null || !owner.isAlive()) {
            return false;
        }

        if (!owner.isOnGround()) {
            return false;
        }

        double distanceSq = entity.squaredDistanceTo(owner);
        return distanceSq < tameableBird.getStartFlyFollowOwnerDistanceSq();
    }

    private boolean hasNearbyThreat(ServerWorld world, T entity) {
        double horizontalRange = entity.getThreatHorizontalRange();
        double verticalRange = entity.getThreatVerticalRange();

        Box box = entity.getBoundingBox().expand(horizontalRange, verticalRange, horizontalRange);

        List<LivingEntity> nearby = world.getEntitiesByClass(
                LivingEntity.class,
                box,
                other -> other != entity && other.isAlive() && entity.isScaredBy(other)
        );

        return !nearby.isEmpty();
    }
}

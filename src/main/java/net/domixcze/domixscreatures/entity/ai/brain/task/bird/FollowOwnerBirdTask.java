package net.domixcze.domixscreatures.entity.ai.brain.task.bird;

import net.domixcze.domixscreatures.entity.ai.brain.util.TameableBird;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class FollowOwnerBirdTask<T extends PathAwareEntity & TameableBird> extends MultiTickTask<T> {

    public FollowOwnerBirdTask() {
        super(Map.of(
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED
        ));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, T entity) {
        if (!entity.isTamed() || entity.isSitting() || entity.hasVehicle()) {
            return false;
        }

        if (entity.getOwner() == null || !entity.getOwner().isAlive()) {
            return false;
        }

        double distanceSq = entity.squaredDistanceTo(entity.getOwner());
        return distanceSq > entity.getStartFollowOwnerDistanceSq();
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, T entity, long time) {
        if (!entity.isTamed() || entity.isSitting() || entity.hasVehicle()) {
            return false;
        }

        if (entity.getOwner() == null || !entity.getOwner().isAlive()) {
            return false;
        }

        double distanceSq = entity.squaredDistanceTo(entity.getOwner());
        return distanceSq > entity.getStopFollowOwnerDistanceSq();
    }

    @Override
    protected void run(ServerWorld world, T entity, long time) {
        this.updateFollow(entity);
    }

    @Override
    protected void keepRunning(ServerWorld world, T entity, long time) {
        this.updateFollow(entity);
    }

    @Override
    protected void finishRunning(ServerWorld world, T entity, long time) {
        entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        entity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    private void updateFollow(T entity) {
        if (entity.getOwner() == null) {
            return;
        }

        var owner = entity.getOwner();
        double distanceSq = entity.squaredDistanceTo(owner);

        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(owner, true));

        if (distanceSq >= entity.getOwnerTeleportDistanceSq() && !entity.isFlying()) {
            if (owner.isOnGround()) {
                if (this.tryTeleportNearOwner(entity)) {
                    entity.getNavigation().stop();
                    entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
                    return;
                }
            } else if (!entity.isBaby() && entity.canStartFlying()) {
                entity.startFlying();
            }
        }

        if (!entity.isFlying() && !entity.isBaby() && distanceSq >= entity.getStartFlyFollowOwnerDistanceSq() && entity.canStartFlying()) {
            entity.startFlying();
        }

        if (entity.isFlying()) {
            Vec3d target = owner.getPos().add(0.0D, 1.5D, 0.0D);
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target, entity.getAirFollowOwnerSpeed(), 1));
        } else {
            entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(owner, false), entity.getGroundFollowOwnerSpeed(), 2));
        }
    }

    private boolean tryTeleportNearOwner(T entity) {
        if (entity.getOwner() == null) {
            return false;
        }

        BlockPos ownerPos = entity.getOwner().getBlockPos();

        for (int i = 0; i < 10; ++i) {
            int offsetX = entity.getRandom().nextBetween(-3, 3);
            int offsetZ = entity.getRandom().nextBetween(-3, 3);

            if (Math.abs(offsetX) < 2 && Math.abs(offsetZ) < 2) {
                continue;
            }

            int offsetY = entity.getRandom().nextBetween(-1, 1);
            BlockPos targetPos = ownerPos.add(offsetX, offsetY, offsetZ);

            if (this.tryTeleportTo(entity, targetPos)) {
                return true;
            }
        }

        return false;
    }

    private boolean tryTeleportTo(T entity, BlockPos pos) {
        if (!this.canTeleportTo(entity, pos)) {
            return false;
        }

        entity.refreshPositionAndAngles(
                pos.getX() + 0.5D,
                pos.getY(),
                pos.getZ() + 0.5D,
                entity.getYaw(),
                entity.getPitch()
        );
        entity.getNavigation().stop();
        return true;
    }

    private boolean canTeleportTo(T entity, BlockPos pos) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(entity, pos);
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        }

        BlockPos offset = pos.subtract(entity.getBlockPos());
        return entity.getWorld().isSpaceEmpty(entity, entity.getBoundingBox().offset(offset));
    }
}

package net.domixcze.domixscreatures.entity.ai.brain.task.bird;

import net.domixcze.domixscreatures.entity.ai.brain.util.Bird;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.List;
import java.util.Map;

public class LandFlightTask<T extends PathAwareEntity & Bird> extends MultiTickTask<T> {

    private BlockPos landingPos;

    public LandFlightTask() {
        super(Map.of(
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED
        ));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, T entity) {
        if (!entity.isFlying() || entity.isLanding()) {
            return false;
        }

        if (entity.shouldStayFlying()) {
            return false;
        }

        if (!entity.canLand()) {
            return false;
        }

        if (this.hasNearbyThreat(world, entity)) {
            return false;
        }

        return entity.getRandom().nextInt(entity.getLandingChanceInterval()) == 0;
    }

    @Override
    protected void run(ServerWorld world, T entity, long time) {
        this.landingPos = this.findLandingSpot(world, entity);

        if (this.landingPos == null) {
            return;
        }

        entity.startLanding();

        Vec3d target = Vec3d.ofCenter(this.landingPos);
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(target));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target, entity.getLandingSpeed(), 0));
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, T entity, long time) {
        if (entity.shouldStayFlying()) {
            return false;
        }

        return entity.isFlying() && entity.isLanding() && this.landingPos != null;
    }

    @Override
    protected void keepRunning(ServerWorld world, T entity, long time) {
        if (this.landingPos == null) {
            return;
        }

        if (entity.shouldStayFlying()) {
            entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
            entity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
            this.landingPos = null;
            return;
        }

        Vec3d target = Vec3d.ofCenter(this.landingPos);
        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(target));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target, entity.getLandingSpeed(), 0));

        if (entity.isOnGround()) {
            entity.stopFlying();
            this.landingPos = null;
        }
    }

    @Override
    protected void finishRunning(ServerWorld world, T entity, long time) {
        this.landingPos = null;
    }

    private BlockPos findLandingSpot(ServerWorld world, T entity) {
        BlockPos origin = entity.getBlockPos();
        int radius = entity.getLandingSearchRadius();

        for (int i = 0; i < entity.getLandingSearchMaxTries(); i++) {
            int x = origin.getX() + entity.getRandom().nextBetween(-radius, radius);
            int z = origin.getZ() + entity.getRandom().nextBetween(-radius, radius);

            BlockPos top = world.getTopPosition(
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    new BlockPos(x, origin.getY(), z)
            );

            BlockPos floorPos = top.down();
            BlockPos landPos = floorPos.up();

            BlockState floor = world.getBlockState(floorPos);

            if (!floor.isSolidBlock(world, floorPos)) {
                continue;
            }

            if (!world.isAir(landPos) || !world.isAir(landPos.up())) {
                continue;
            }

            return landPos;
        }

        return null;
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

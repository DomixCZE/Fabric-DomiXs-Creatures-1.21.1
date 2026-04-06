package net.domixcze.domixscreatures.entity.ai.brain.task.bird;

import net.domixcze.domixscreatures.entity.ai.brain.util.Bird;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

import java.util.Map;

public class AirWanderFlightTask<T extends PathAwareEntity & Bird> extends MultiTickTask<T> {

    private int retargetCooldown = 0;

    public AirWanderFlightTask() {
        super(Map.of(
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED
        ));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, T entity) {
        return entity.isFlying() && !entity.isLanding();
    }

    @Override
    protected void run(ServerWorld world, T entity, long time) {
        this.trySetNewTarget(world, entity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, T entity, long time) {
        return entity.isFlying() && !entity.isLanding();
    }

    @Override
    protected void keepRunning(ServerWorld world, T entity, long time) {
        if (this.retargetCooldown > 0) {
            this.retargetCooldown--;
            return;
        }

        WalkTarget walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        if (walkTarget == null) {
            this.trySetNewTarget(world, entity);
            return;
        }

        double distSq = entity.squaredDistanceTo(
                walkTarget.getLookTarget().getPos().x,
                walkTarget.getLookTarget().getPos().y,
                walkTarget.getLookTarget().getPos().z
        );

        if (distSq < entity.getAirWanderReachedDistanceSq()) {
            this.trySetNewTarget(world, entity);
        }
    }

    private void trySetNewTarget(ServerWorld world, T entity) {
        Vec3d target = this.findAirTarget(world, entity);
        if (target == null) {
            return;
        }

        entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(target));
        entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target, entity.getAirWanderSpeed(), 1));

        int min = entity.getAirWanderRetargetMinTicks();
        int max = entity.getAirWanderRetargetMaxTicks();
        this.retargetCooldown = min + entity.getRandom().nextInt(Math.max(1, max - min + 1));
    }

    private Vec3d findAirTarget(ServerWorld world, T entity) {
        BlockPos origin = entity.getBlockPos();
        int range = entity.getAirWanderHorizontalRange();

        for (int i = 0; i < entity.getAirWanderTargetMaxTries(); i++) {
            int x = origin.getX() + entity.getRandom().nextBetween(-range, range);
            int z = origin.getZ() + entity.getRandom().nextBetween(-range, range);

            int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            int minY = topY + entity.getAirWanderMinHeightAboveGround();
            int maxY = topY + entity.getAirWanderMaxHeightAboveGround();
            int baseY = entity.getRandom().nextBetween(minY, maxY);

            int jitter = entity.getAirWanderHeightJitter();
            if (jitter > 0) {
                baseY += entity.getRandom().nextBetween(-jitter, jitter);
            }

            int y = Math.max(origin.getY() - 1, baseY);

            BlockPos pos = new BlockPos(x, y, z);

            if (world.isAir(pos) && world.isAir(pos.up())) {
                return Vec3d.ofCenter(pos);
            }
        }

        return null;
    }
}

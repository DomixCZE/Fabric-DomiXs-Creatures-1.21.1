package net.domixcze.domixscreatures.entity.ai.goal;

import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.entity.ai.goal.util.ColonyMember;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;

public class StayNearColonyGoal<T extends MobEntity & ColonyMember> extends Goal {
    private final T entity;
    private final double speed;
    private final int innerRadius;
    private final int totalRadius;
    private final Block nestBlock;

    private BlockPos targetWaypoint;

    public StayNearColonyGoal(T entity, Block nestBlock, double speed, int innerRadius, int outerRadiusOffset) {
        this.entity = entity;
        this.nestBlock = nestBlock;
        this.speed = speed;
        this.innerRadius = innerRadius;
        this.totalRadius = innerRadius + outerRadiusOffset;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (entity.isLeashed()) return false;

        BlockPos currentHome = entity.getHomePos();

        if (currentHome.equals(BlockPos.ORIGIN) || isHomeBlockInvalid(currentHome)) {
            findNearestNestAroundEntity(totalRadius).ifPresentOrElse(
                    entity::setHomePos,
                    () -> entity.setHomePos(BlockPos.ORIGIN)
            );
            currentHome = entity.getHomePos();
        }

        if (currentHome.equals(BlockPos.ORIGIN)) return false;

        double distSqToHome = entity.getBlockPos().getSquaredDistance(currentHome.getX() + 0.5, currentHome.getY(), currentHome.getZ() + 0.5);
        return distSqToHome > (double) innerRadius * innerRadius;
    }

    @Override
    public void start() {
        moveTowardsHome();
    }

    @Override
    public void tick() {
        double distSqToHome = entity.getBlockPos().getSquaredDistance(entity.getHomePos().getX() + 0.5, entity.getHomePos().getY(), entity.getHomePos().getZ() + 0.5);

        if (entity.getNavigation().isIdle() && distSqToHome > (double) innerRadius * innerRadius) {
            moveTowardsHome();
        }
    }

    private void moveTowardsHome() {
        this.targetWaypoint = findRandomPositionNearHome(entity.getHomePos(), innerRadius);
        if (this.targetWaypoint != null) {
            entity.getNavigation().startMovingTo(
                    this.targetWaypoint.getX() + 0.5, this.targetWaypoint.getY(), this.targetWaypoint.getZ() + 0.5, speed
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        if (entity.isLeashed() || entity.isRemoved()) return false;

        BlockPos currentHome = entity.getHomePos();
        if (currentHome.equals(BlockPos.ORIGIN) || isHomeBlockInvalid(currentHome)) return false;

        double distSqToHome = entity.getBlockPos().getSquaredDistance(currentHome.getX() + 0.5, currentHome.getY(), currentHome.getZ() + 0.5);

        if (distSqToHome > (double) totalRadius * totalRadius) {
            BlockPos home = entity.getHomePos();
            if (entity.getWorld().getBlockEntity(home) instanceof AbstractColonyBlockEntity colony) {
                colony.unregisterMember(entity.getUuid());
            }
            entity.setHomePos(BlockPos.ORIGIN);
            return false;
        }

        return distSqToHome > (double) innerRadius * innerRadius;
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
        this.targetWaypoint = null;
    }

    private boolean isHomeBlockInvalid(BlockPos homePos) {
        World world = entity.getWorld();
        if (!world.isChunkLoaded(homePos)) return false;
        return !world.getBlockState(homePos).isOf(nestBlock);
    }

    private Optional<BlockPos> findNearestNestAroundEntity(int searchRadius) {
        BlockPos pos = entity.getBlockPos();
        World world = entity.getWorld();

        for (BlockPos p : BlockPos.iterate(pos.add(-searchRadius, -2, -searchRadius), pos.add(searchRadius, 2, searchRadius))) {
            if (world.getBlockState(p).isOf(nestBlock)) {
                if (world.getBlockEntity(p) instanceof AbstractColonyBlockEntity colony) {
                    if (colony.registerMember(entity.getUuid())) {
                        return Optional.of(p.toImmutable());
                    }
                }
            }
        }
        return Optional.empty();
    }

    private BlockPos findRandomPositionNearHome(BlockPos center, int radius) {
        World world = entity.getWorld();
        for (int i = 0; i < 10; i++) {
            BlockPos randomPos = center.add(
                    world.getRandom().nextInt(radius * 2 + 1) - radius,
                    world.getRandom().nextInt(3) - 1,
                    world.getRandom().nextInt(radius * 2 + 1) - radius
            );

            if (world.getBlockState(randomPos).isAir() && world.getBlockState(randomPos.down()).isSolidBlock(world, randomPos.down())) {
                return randomPos;
            }
        }
        return null;
    }
}
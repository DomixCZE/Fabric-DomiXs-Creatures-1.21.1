package net.domixcze.domixscreatures.entity.ai.goal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.function.BiPredicate;

public abstract class MoveToBlockGoal extends Goal {

    public int MAX_TRY_TICKS = 60;
    public int SEARCH_RADIUS;
    public int VERTICAL_RANGE; // how many blocks to check above and below

    protected final PathAwareEntity entity;
    protected final World world;

    protected BlockPos targetPos;
    protected int ticksTrying;

    protected final BiPredicate<BlockState, BlockPos> blockPredicate;

    public MoveToBlockGoal(PathAwareEntity entity, BiPredicate<BlockState, BlockPos> blockPredicate, int searchRadius, int verticalRange) {
        this.entity = entity;
        this.world = entity.getWorld();
        this.blockPredicate = blockPredicate;
        this.SEARCH_RADIUS = searchRadius;
        this.VERTICAL_RANGE = verticalRange;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public MoveToBlockGoal(PathAwareEntity entity, Block targetBlock, int searchRadius, int verticalRange) {
        this(entity, (state, pos) -> state.isOf(targetBlock), searchRadius, verticalRange);
    }

    public MoveToBlockGoal(PathAwareEntity entity, TagKey<Block> blockTag, int searchRadius, int verticalRange) {
        this(entity, (state, pos) -> state.isIn(blockTag), searchRadius, verticalRange);
    }

    @Override
    public boolean canStart() {
        if (!entity.isOnGround()) return false;

        BlockPos below = entity.getBlockPos().down();
        if (blockPredicate.test(world.getBlockState(below), below) && world.getBlockState(below.up()).isAir()) {
            targetPos = below;
            return true;
        }

        targetPos = findTargetBlock();
        return targetPos != null;
    }

    @Override
    public void start() {
        ticksTrying = 0;
        moveToTarget();
    }

    @Override
    public void tick() {
        if (targetPos == null) return;
        ticksTrying++;

        Vec3d pos = entity.getPos();
        double dx = pos.x - (targetPos.getX() + 0.5);
        double dz = pos.z - (targetPos.getZ() + 0.5);
        double horizontalDistSq = dx * dx + dz * dz;

        if (horizontalDistSq <= 1.2 * 1.2 && entity.isOnGround()) {
            entity.getNavigation().stop();
            onReachedTarget(targetPos);
            return;
        }

        if (ticksTrying > MAX_TRY_TICKS || entity.getNavigation().isIdle()) {
            stop();
        }
    }

    @Override
    public void stop() {
        targetPos = null;
        ticksTrying = 0;
    }

    protected void moveToTarget() {
        Vec3d navTarget = Vec3d.ofCenter(targetPos).add(0, 1, 0);
        entity.getNavigation().startMovingTo(navTarget.x, navTarget.y, navTarget.z, 0.8D);
    }

    protected BlockPos findTargetBlock() {
        BlockPos origin = entity.getBlockPos();
        Random random = entity.getRandom();

        for (int i = 0; i < 20; i++) {
            int offsetX = random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;
            int offsetY = random.nextInt(VERTICAL_RANGE * 2 + 1) - VERTICAL_RANGE;
            int offsetZ = random.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS;

            BlockPos pos = origin.add(offsetX, offsetY, offsetZ);
            BlockState state = world.getBlockState(pos);

            if (blockPredicate.test(state, pos) && world.getBlockState(pos.up()).isAir()) {
                return pos;
            }
        }
        return null;
    }

    protected abstract void onReachedTarget(BlockPos pos);
}
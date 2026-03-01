package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.block.custom.HollowLogBlock;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.entity.client.ant.AntVariants;
import net.domixcze.domixscreatures.entity.client.termite.TermiteVariants;
import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.EnumSet;

public class ReturnToColonyGoal<T extends MobEntity & ColonyMember> extends Goal {
    private final T entity;
    private final double speed;

    public ReturnToColonyGoal(T entity, double speed) {
        this.entity = entity;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        BlockPos home = entity.getHomePos();
        if (home == null || home.equals(BlockPos.ORIGIN)) return false;

        if (entity instanceof TermiteEntity termite) {
            TermiteVariants variant = termite.getVariant();

            if (variant == TermiteVariants.QUEEN) {
                if (entity.getWorld().getBlockEntity(home) instanceof AbstractColonyBlockEntity colony) {
                    return !colony.hasQueen();
                }
                return false;
            }

            if (variant == TermiteVariants.WORKER && termite.hasSawdust()) {
                return true;
            }

            if (variant == TermiteVariants.SOLDIER && termite.getHealth() <= termite.getMaxHealth() / 2f) {
                return true;
            }
        }

        if (entity instanceof AntEntity ant) {
            AntVariants variant = ant.getVariant();

            if (variant == AntVariants.QUEEN) {
                if (entity.getWorld().getBlockEntity(home) instanceof AbstractColonyBlockEntity colony) {
                    return !colony.hasQueen();
                }
                return false;
            }

            if (variant == AntVariants.WORKER && ant.hasLeaves()) {
                return true;
            }

            if (variant == AntVariants.SOLDIER && ant.getHealth() <= ant.getMaxHealth() / 2f) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        if (entity instanceof TermiteEntity) {
            BlockPos pos = entity.getBlockPos();
            if (entity.getWorld().getBlockState(pos).getBlock() instanceof HollowLogBlock) {
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockPos neighbor = pos.offset(dir);
                    if (entity.getWorld().isAir(neighbor)) {
                        entity.refreshPositionAndAngles(neighbor.getX() + 0.5, entity.getY(), neighbor.getZ() + 0.5, entity.getYaw(), entity.getPitch());
                        break;
                    }
                }
            }
        }

        BlockPos home = entity.getHomePos();
        entity.getNavigation().startMovingTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, speed);
    }

    @Override
    public void tick() {
        BlockPos home = entity.getHomePos();

        if (entity.getNavigation().isIdle() || entity.age % 10 == 0) {
            entity.getNavigation().startMovingTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5, speed);
        }

        if (entity.getBlockPos().isWithinDistance(home, 8.0)) {
            if (entity.getWorld().getBlockEntity(home) instanceof AbstractColonyBlockEntity colony) {

                BlockPos currentPos = entity.getBlockPos();
                BlockState stateAt = entity.getWorld().getBlockState(currentPos);
                BlockState stateBelow = entity.getWorld().getBlockState(currentPos.down());

                if (stateAt.isOf(colony.getColonyMound()) || stateBelow.isOf(colony.getColonyMound())) {

                    if (entity instanceof TermiteEntity termite && termite.getVariant() == TermiteVariants.QUEEN) {
                        if (!colony.hasQueen()) {
                            colony.setHasQueen(true);
                            termite.discard();
                        }
                    }
                    else if (entity instanceof AntEntity ant && ant.getVariant() == AntVariants.QUEEN) {
                        if (!colony.hasQueen()) {
                            colony.setHasQueen(true);
                            ant.discard();
                        }
                    }
                    else {
                        colony.tryEnter(entity);
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return canStart() && !entity.isRemoved();
    }
}
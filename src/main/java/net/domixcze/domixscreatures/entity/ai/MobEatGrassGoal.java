package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.EnumSet;

public class MobEatGrassGoal extends Goal {
    private static final int MAX_TIMER = 40;
    private final MobEntity mob;
    private final World world;
    private int timer;

    public MobEatGrassGoal(MobEntity mob) {
        this.mob = mob;
        this.world = mob.getWorld();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 200 : 1200) != 0) {
            return false;
        }

        if (this.mob instanceof EatsGrass eater && !eater.canEatGrass()) {
            return false;
        }

        BlockPos pos = mob.getBlockPos();
        BlockState state = world.getBlockState(pos);
        BlockState below = world.getBlockState(pos.down());

        return state.isIn(ModTags.Blocks.CAN_EAT) || below.isIn(ModTags.Blocks.CAN_EAT);
    }

    @Override
    public void start() {
        this.timer = MAX_TIMER;
        this.mob.getNavigation().stop();

        if (mob instanceof EatsGrass eater) {
            eater.setEating(true);
        }
    }

    @Override
    public void stop() {
        this.timer = 0;

        if (mob instanceof EatsGrass eater) {
            eater.setEating(false);
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.timer > 0 && this.mob.getVelocity().horizontalLengthSquared() < 1.0E-9;
    }

    @Override
    public void tick() {
        // Check if the block below is still edible
        BlockPos currentPos = mob.getBlockPos();
        BlockState currentState = world.getBlockState(currentPos);
        BlockState belowState = world.getBlockState(currentPos.down());
        boolean canEatHere = currentState.isIn(ModTags.Blocks.CAN_EAT) || belowState.isIn(ModTags.Blocks.CAN_EAT);

        if (!canEatHere) {
            this.stop();
            return;
        }

        timer = Math.max(0, timer - 1);

        if (timer == 4) {
            BlockPos pos = mob.getBlockPos();
            BlockPos below = pos.down();

            if (world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                if (world.getBlockState(pos).isIn(ModTags.Blocks.CAN_EAT)) {
                    world.breakBlock(pos, false);
                } else if (world.getBlockState(below).isIn(ModTags.Blocks.CAN_EAT)) {
                    world.setBlockState(below, Blocks.DIRT.getDefaultState(), Block.NOTIFY_LISTENERS);
                }
            }

            if (mob instanceof EatsGrass eater) {
                eater.onEatGrass();
            }
        }
    }
}
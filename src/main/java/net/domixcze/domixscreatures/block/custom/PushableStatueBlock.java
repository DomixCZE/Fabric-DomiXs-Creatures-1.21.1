package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PushableStatueBlock extends Block {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public PushableStatueBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        BlockState below = world.getBlockState(pos.down());
        boolean shouldBePowered = below.isOf(ModBlocks.ACTIVATOR_TRACK_BLOCK) || below.isOf(Blocks.REDSTONE_BLOCK);

        if (state.get(POWERED) != shouldBePowered) {
            world.setBlockState(pos, state.with(POWERED, shouldBePowered), Block.NOTIFY_ALL);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient) return;

        BlockState below = world.getBlockState(pos.down());
        boolean shouldBePowered = below.isOf(ModBlocks.ACTIVATOR_TRACK_BLOCK) || below.isOf(Blocks.REDSTONE_BLOCK);

        if (state.get(POWERED) != shouldBePowered) {
            world.setBlockState(pos, state.with(POWERED, shouldBePowered), Block.NOTIFY_ALL);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {

        if (world.isClient) return ActionResult.SUCCESS;

        Direction pushDir = hit.getSide();
        Direction moveDir = pushDir.getOpposite();

        BlockPos targetPos = pos.offset(moveDir);

        BlockPos belowTarget = targetPos.down();
        boolean validBelow = world.getBlockState(belowTarget).isOf(ModBlocks.TRACK_BLOCK)
                || world.getBlockState(belowTarget).isOf(ModBlocks.ACTIVATOR_TRACK_BLOCK);

        if (world.isAir(targetPos) && validBelow) {
            world.setBlockState(targetPos, state, Block.NOTIFY_ALL);
            world.removeBlock(pos, false);

            world.playSound(
                    null,
                    targetPos,
                    ModSounds.STATUE_PUSH,
                    SoundCategory.BLOCKS,
                    1.0f,
                    0.8f + world.random.nextFloat() * 0.4f
            );
        }

        return ActionResult.SUCCESS;
    }
}
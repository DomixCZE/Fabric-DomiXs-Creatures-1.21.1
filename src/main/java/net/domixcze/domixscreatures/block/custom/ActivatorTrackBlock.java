package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ActivatorTrackBlock extends Block {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public ActivatorTrackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        this.updatePower(world, pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        this.updatePower(world, pos, state);
    }

    private void updatePower(World world, BlockPos pos, BlockState state) {
        boolean hasStatue = world.getBlockState(pos.up()).isOf(ModBlocks.PUSHABLE_STATUE_BLOCK);
        boolean isPowered = state.get(POWERED);

        if (hasStatue != isPowered) {
            world.setBlockState(pos, state.with(POWERED, hasStatue), Block.NOTIFY_ALL);
            world.updateNeighborsAlways(pos, this);
            world.updateNeighborsAlways(pos.down(), this);
            world.updateNeighborsAlways(pos.up(), this);
        }
    }
}
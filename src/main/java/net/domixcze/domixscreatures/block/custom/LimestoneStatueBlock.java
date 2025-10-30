package net.domixcze.domixscreatures.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public class LimestoneStatueBlock extends Block {
    public static final MapCodec<LimestoneStatueBlock> CODEC = createCodec(LimestoneStatueBlock::new);
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 3);

    public LimestoneStatueBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getPlayerLookDirection().getOpposite();
        BlockState state = this.getDefaultState().with(FACING, facing);

        if (facing == Direction.UP || facing == Direction.DOWN) {
            int rotation = Math.floorMod(Math.round(ctx.getPlayerYaw() / 90f), 4);
            state = state.with(ROTATION, rotation);
        }

        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
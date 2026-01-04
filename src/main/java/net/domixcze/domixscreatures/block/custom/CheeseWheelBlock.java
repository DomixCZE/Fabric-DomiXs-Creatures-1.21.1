package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CheeseWheelBlock extends Block implements Waterloggable {

    public static final IntProperty STAGE = IntProperty.of("stage", 1, 4);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape STAGE_4 = Block.createCuboidShape(2, 0, 2, 14, 5, 14);
    private static final VoxelShape STAGE_3 = VoxelShapes.union(
            Block.createCuboidShape(8, 0, 2, 14, 5, 14),
            Block.createCuboidShape(2, 0, 8, 8, 5, 14)
    );
    private static final VoxelShape STAGE_2 = Block.createCuboidShape(8, 0, 2, 14, 5, 14);
    private static final VoxelShape STAGE_1 = Block.createCuboidShape(8, 0, 2, 14, 5, 8);

    private static final VoxelShape[] SHAPES = { null, STAGE_1, STAGE_2, STAGE_3, STAGE_4 };

    public CheeseWheelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(STAGE, 4)
                .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluid = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return this.getDefaultState()
                .with(STAGE, 4)
                .with(WATERLOGGED, fluid.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(STAGE)];
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            world.playSound(null, pos, SoundEvents.BLOCK_WOOL_HIT,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);

            ItemStack slice = new ItemStack(ModItems.CHEESE_PIECE);
            if (!player.getInventory().insertStack(slice)) {
                player.dropItem(slice, false);
            }

            int stage = state.get(STAGE);
            if (stage > 1) {
                world.setBlockState(pos, state.with(STAGE, stage - 1), Block.NOTIFY_ALL);
            } else {
                world.removeBlock(pos, false);
            }
        }
        return ActionResult.SUCCESS;
    }
}
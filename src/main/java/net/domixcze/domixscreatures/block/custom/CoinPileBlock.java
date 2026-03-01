package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CoinPileBlock extends Block {

    public static final DirectionProperty FACING = DirectionProperty.of(
            "facing",
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    );

    public static final IntProperty LEVEL = IntProperty.of("level", 1, 4);

    private static final VoxelShape SHAPE_LEVEL_1 =
            Block.createCuboidShape(2, 0, 2, 14, 3, 14);
    private static final VoxelShape SHAPE_LEVEL_2 =
            Block.createCuboidShape(2, 0, 2, 14, 6, 14);
    private static final VoxelShape SHAPE_LEVEL_3 =
            Block.createCuboidShape(2, 0, 2, 14, 8, 14);
    private static final VoxelShape SHAPE_LEVEL_4 =
            Block.createCuboidShape(2, 0, 2, 14, 10, 14);


    public CoinPileBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(LEVEL, 1));
    }


    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(LEVEL, 1);
    }

    @Override
    protected ItemActionResult onUseWithItem(
            ItemStack stack,
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit
    ) {
        if (stack.isOf(ModItems.GOLD_COIN)) {
            int level = state.get(LEVEL);

            if (level < 4) {
                if (!world.isClient) {
                    world.setBlockState(pos, state.with(LEVEL, level + 1), Block.NOTIFY_ALL);

                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                }
                return ItemActionResult.success(world.isClient);
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(LEVEL)) {
            case 2 -> SHAPE_LEVEL_2;
            case 3 -> SHAPE_LEVEL_3;
            case 4 -> SHAPE_LEVEL_4;
            default -> SHAPE_LEVEL_1;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEVEL);
    }
}
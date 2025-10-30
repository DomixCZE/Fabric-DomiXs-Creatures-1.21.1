package net.domixcze.domixscreatures.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class TableClothBlock extends Block {
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public TableClothBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        return !world.isAir(below) && world.getBlockState(below).isSideSolidFullSquare(world, below, Direction.UP);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        WorldAccess world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(NORTH, shouldHaveOverhang(world, pos, Direction.NORTH))
                .with(SOUTH, shouldHaveOverhang(world, pos, Direction.SOUTH))
                .with(EAST,  shouldHaveOverhang(world, pos, Direction.EAST))
                .with(WEST,  shouldHaveOverhang(world, pos, Direction.WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            if (world instanceof World w && !w.isClient) {
                dropStack(w, pos, new ItemStack(this));
            }
            return Blocks.AIR.getDefaultState();
        }

        if (direction.getAxis().isHorizontal()) {
            return state.with(getPropertyForDirection(direction), shouldHaveOverhang(world, pos, direction));
        }

        return state;
    }

    private boolean shouldHaveOverhang(WorldAccess world, BlockPos pos, Direction dir) {
        BlockPos sidePos = pos.offset(dir);
        BlockState sideState = world.getBlockState(sidePos);

        return !sideState.isSideSolidFullSquare(world, sidePos, dir.getOpposite())
                && !(sideState.getBlock() instanceof TableClothBlock);
    }

    private BooleanProperty getPropertyForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            default -> throw new IllegalArgumentException("Invalid direction: " + dir);
        };
    }
}
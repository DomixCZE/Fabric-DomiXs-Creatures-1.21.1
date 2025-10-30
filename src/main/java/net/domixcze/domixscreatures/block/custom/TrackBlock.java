package net.domixcze.domixscreatures.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class TrackBlock extends Block {
    public static final EnumProperty<TrackShape> SHAPE = EnumProperty.of("shape", TrackShape.class);

    public TrackBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, TrackShape.STRAIGHT_NS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(SHAPE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.updateShape(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world,
                                                BlockPos pos, BlockPos neighborPos) {
        return this.updateShape(world, pos, state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(SHAPE, state.get(SHAPE).rotate(rotation));
    }

    private BlockState updateShape(WorldAccess world, BlockPos pos, BlockState state) {
        boolean north = isTrack(world, pos.north());
        boolean south = isTrack(world, pos.south());
        boolean east = isTrack(world, pos.east());
        boolean west = isTrack(world, pos.west());

        int count = (north ? 1 : 0) + (south ? 1 : 0) + (east ? 1 : 0) + (west ? 1 : 0);

        if (count == 4) return state.with(SHAPE, TrackShape.CROSS);
        if (count == 3) {
            if (!north) return state.with(SHAPE, TrackShape.T_SOUTH);
            if (!south) return state.with(SHAPE, TrackShape.T_NORTH);
            if (!east)  return state.with(SHAPE, TrackShape.T_WEST);
            return state.with(SHAPE, TrackShape.T_EAST);
        }
        if (count == 2) {
            if (north && south) return state.with(SHAPE, TrackShape.STRAIGHT_NS);
            if (east && west)   return state.with(SHAPE, TrackShape.STRAIGHT_EW);
            if (north && east)  return state.with(SHAPE, TrackShape.CURVE_NE);
            if (north && west)  return state.with(SHAPE, TrackShape.CURVE_NW);
            if (south && east)  return state.with(SHAPE, TrackShape.CURVE_SE);
            if (south && west)  return state.with(SHAPE, TrackShape.CURVE_SW);
        }
        if (count == 1) {
            if (north || south) return state.with(SHAPE, TrackShape.STRAIGHT_NS);
            if (east || west)   return state.with(SHAPE, TrackShape.STRAIGHT_EW);
        }
        return state;
    }

    private boolean isTrack(WorldAccess world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block instanceof TrackBlock || block instanceof ActivatorTrackBlock;
    }

    public enum TrackShape implements StringIdentifiable {
        STRAIGHT_NS("straight_ns"),
        STRAIGHT_EW("straight_ew"),
        CURVE_NE("curve_ne"),
        CURVE_NW("curve_nw"),
        CURVE_SE("curve_se"),
        CURVE_SW("curve_sw"),
        T_NORTH("t_north"),
        T_EAST("t_east"),
        T_SOUTH("t_south"),
        T_WEST("t_west"),
        CROSS("cross");

        private final String name;

        TrackShape(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }

        public TrackShape rotate(BlockRotation rotation) {
            return switch (this) {
                case STRAIGHT_NS -> switch (rotation) {
                    case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> STRAIGHT_EW;
                    default -> this;
                };
                case STRAIGHT_EW -> switch (rotation) {
                    case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> STRAIGHT_NS;
                    default -> this;
                };
                case CURVE_NE -> switch (rotation) {
                    case CLOCKWISE_90 -> CURVE_SE;
                    case CLOCKWISE_180 -> CURVE_SW;
                    case COUNTERCLOCKWISE_90 -> CURVE_NW;
                    default -> this;
                };
                case CURVE_SE -> switch (rotation) {
                    case CLOCKWISE_90 -> CURVE_SW;
                    case CLOCKWISE_180 -> CURVE_NW;
                    case COUNTERCLOCKWISE_90 -> CURVE_NE;
                    default -> this;
                };
                case CURVE_SW -> switch (rotation) {
                    case CLOCKWISE_90 -> CURVE_NW;
                    case CLOCKWISE_180 -> CURVE_NE;
                    case COUNTERCLOCKWISE_90 -> CURVE_SE;
                    default -> this;
                };
                case CURVE_NW -> switch (rotation) {
                    case CLOCKWISE_90 -> CURVE_NE;
                    case CLOCKWISE_180 -> CURVE_SE;
                    case COUNTERCLOCKWISE_90 -> CURVE_SW;
                    default -> this;
                };
                case T_NORTH -> switch (rotation) {
                    case CLOCKWISE_90 -> T_EAST;
                    case CLOCKWISE_180 -> T_SOUTH;
                    case COUNTERCLOCKWISE_90 -> T_WEST;
                    default -> this;
                };
                case T_EAST -> switch (rotation) {
                    case CLOCKWISE_90 -> T_SOUTH;
                    case CLOCKWISE_180 -> T_WEST;
                    case COUNTERCLOCKWISE_90 -> T_NORTH;
                    default -> this;
                };
                case T_SOUTH -> switch (rotation) {
                    case CLOCKWISE_90 -> T_WEST;
                    case CLOCKWISE_180 -> T_NORTH;
                    case COUNTERCLOCKWISE_90 -> T_EAST;
                    default -> this;
                };
                case T_WEST -> switch (rotation) {
                    case CLOCKWISE_90 -> T_NORTH;
                    case CLOCKWISE_180 -> T_EAST;
                    case COUNTERCLOCKWISE_90 -> T_SOUTH;
                    default -> this;
                };
                case CROSS -> this;
            };
        }
    }
}
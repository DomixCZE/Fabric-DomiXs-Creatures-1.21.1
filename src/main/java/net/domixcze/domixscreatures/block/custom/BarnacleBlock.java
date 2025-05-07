package net.domixcze.domixscreatures.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BarnacleBlock extends Block implements Waterloggable {
    protected static final VoxelShape ONE_BARNACLE_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 3.0, 10.0);
    protected static final VoxelShape TWO_BARNACLE_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0);
    protected static final VoxelShape THREE_BARNACLE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);
    protected static final VoxelShape FOUR_BARNACLE_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0);

    public static final IntProperty BARNACLE_COUNT = IntProperty.of("barnacles", 1, 4);
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty SHEARED = BooleanProperty.of("sheared");

    public BarnacleBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState()
                .with(BARNACLE_COUNT, 1)
                .with(WATERLOGGED, false)
                .with(SHEARED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(BARNACLE_COUNT, WATERLOGGED, SHEARED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockPos blockBelow = pos.down();
        FluidState fluidState = world.getFluidState(pos);

        if (fluidState.getFluid() == Fluids.WATER) {
            BlockState blockStateBelow = world.getBlockState(blockBelow);
            if (blockStateBelow.isFullCube(world, blockBelow)) {
                return this.getDefaultState().with(BARNACLE_COUNT, 1).with(WATERLOGGED, true);
            }
        }
        return null;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            ((World)world).scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(BARNACLE_COUNT)) {
            case 1:
            default:
                return ONE_BARNACLE_SHAPE;
            case 2:
                return TWO_BARNACLE_SHAPE;
            case 3:
                return THREE_BARNACLE_SHAPE;
            case 4:
                return FOUR_BARNACLE_SHAPE;
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack itemStack = player.getMainHandStack();

        if (itemStack.isOf(Items.SHEARS)) {
            boolean sheared = state.get(SHEARED);

            if (!sheared) {
                world.setBlockState(pos, state.with(SHEARED, true), 3);
                itemStack.damage(1, player, EquipmentSlot.MAINHAND);
                world.playSound(player, pos, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, SoundCategory.BLOCKS, 1.0F, 1.0F);

                return ActionResult.SUCCESS;
            }
            return  ActionResult.PASS;
        }

        if (itemStack.isOf(world.getBlockState(pos).getBlock().asItem())) {
            int currentCount = state.get(BARNACLE_COUNT);
            boolean sheared = state.get(SHEARED);

            if (currentCount < 4 && !sheared) {
                world.setBlockState(pos, state.with(BARNACLE_COUNT, currentCount + 1), 3);

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                world.playSound(player, pos, SoundEvents.BLOCK_POINTED_DRIPSTONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(player, GameEvent.BLOCK_PLACE, pos);

                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.get(WATERLOGGED) && isOnLog(world, pos)) {
            world.scheduleBlockTick(pos, this, getGrowthTime());
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (state.get(WATERLOGGED) && !state.get(SHEARED) && isOnLog(world, pos)) {
            int currentCount = state.get(BARNACLE_COUNT);

            if (currentCount < 4) {
                world.setBlockState(pos, state.with(BARNACLE_COUNT, currentCount + 1), 3);
                world.scheduleBlockTick(pos, this, getGrowthTime());
            }
        }
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        this.breakBarnacle(world, pos, state);
    }

    private void breakBarnacle(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_DRIPSTONE_BLOCK_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = state.get(BARNACLE_COUNT);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, state.with(BARNACLE_COUNT, i - 1),2);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }

    private boolean isOnLog(World world, BlockPos pos) {
        BlockState blockBelow = world.getBlockState(pos.down());
        return blockBelow.isIn(BlockTags.LOGS);
    }

    private int getGrowthTime() {
        return 6000 + new Random().nextInt(6000);
    }
}
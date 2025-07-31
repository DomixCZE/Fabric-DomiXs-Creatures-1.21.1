package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

public class CoconutBlock extends Block implements LandingBlock, Waterloggable {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty ATTACHED = BooleanProperty.of("attached");
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CoconutBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false)
                .with(ATTACHED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, ATTACHED, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction side = ctx.getSide();
        BlockPos supportPos = pos.offset(side.getOpposite());
        BlockState support = world.getBlockState(supportPos);

        FluidState fluidState = world.getFluidState(pos);
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;

        if (side == Direction.UP && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            // On top of solid block
            return this.getDefaultState()
                    .with(FACING, Direction.UP)
                    .with(ATTACHED, false)
                    .with(WATERLOGGED, waterlogged);
        } else if (side.getAxis().isHorizontal() && support.isOf(ModBlocks.PALM_LOG)) {
            // Attached to side of log
            return this.getDefaultState()
                    .with(FACING, side.getOpposite())
                    .with(ATTACHED, true)
                    .with(WATERLOGGED, waterlogged);
        }

        return null;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient && !oldState.isOf(state.getBlock())) {
            world.scheduleBlockTick(pos, this, 1);
        }
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
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

        boolean attached = state.get(ATTACHED);
        Direction facing = state.get(FACING);

        if (attached && direction == facing) {
            if (!neighborState.isOf(ModBlocks.PALM_LOG)) {
                world.scheduleBlockTick(pos, this, 1);
            }
        } else if (!attached && direction == Direction.DOWN) {
            if (!neighborState.isSolidBlock(world, neighborPos)) {
                world.scheduleBlockTick(pos, this, 1);
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient) return;

        boolean shouldFall = false;
        boolean attached = state.get(ATTACHED);
        Direction facing = state.get(FACING);

        if (attached) {
            if (!world.getBlockState(pos.offset(facing)).isOf(ModBlocks.PALM_LOG)) {
                shouldFall = true;
            }
        } else {
            if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                shouldFall = true;
            }
        }

        if (shouldFall) {
            spawnFallingCoconut(world, pos, state.with(ATTACHED, false));
        }
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient && state.get(ATTACHED)) {
            FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, hit.getBlockPos(), state.with(ATTACHED, false));
            falling.setHurtEntities(2.0F, 10);
        }
    }

    private void spawnFallingCoconut(World world, BlockPos pos, BlockState state) {
        FallingBlockEntity falling = FallingBlockEntity.spawnFromBlock(world, pos, state);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack.isIn(ModTags.Items.CAN_BREAK_COCONUT)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (world.isClient) {
            return ItemActionResult.success(true);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);

        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.COCONUT_SLICE, 2));

        world.breakBlock(pos, false);
        stack.damage(1, player, LivingEntity.getSlotForHand(hand));
        world.emitGameEvent(player, GameEvent.BLOCK_DESTROY, pos);

        return ItemActionResult.success(false);
    }

    @Override
    public void onLanding(World world, BlockPos pos, BlockState fallingState, BlockState currentStateInWorld, FallingBlockEntity entity) {
        SoundEvent landSound = ModConfig.INSTANCE.enableCoconutBongSound ? ModSounds.COCONUT_LAND : SoundEvents.BLOCK_WOOD_PLACE;
        world.playSound(null, pos, landSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity entity) {
        world.playSound(null, pos, SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean attached = state.get(ATTACHED);
        Direction facing = state.get(FACING);

        if (attached) {
            return switch (facing) {
                case NORTH -> Block.createCuboidShape(4, 0, 1, 12, 8, 9);
                case SOUTH -> Block.createCuboidShape(4, 0, 7, 12, 8, 15);
                case EAST -> Block.createCuboidShape(7, 0, 4, 15, 8, 12);
                case WEST -> Block.createCuboidShape(1, 0, 4, 9, 8, 12);
                default -> Block.createCuboidShape(4, 0, 1, 12, 8, 9);
            };
        } else {
            return Block.createCuboidShape(4, 0, 4, 12, 8, 12);
        }
    }
}
package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.crocodile.CrocodileVariants;
import net.domixcze.domixscreatures.entity.custom.CrocodileEntity;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class CrocodileEggBlock extends Block {
    public static final IntProperty HATCH;
    public static final IntProperty EGGS = IntProperty.of("eggs", 1, 3);
    private static final VoxelShape ONE_EGG_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 12.0, 5.0, 12.0);
    private static final VoxelShape TWO_EGG_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 6.0, 15.0);
    private static final VoxelShape THREE_EGG_SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 6.0, 15.0);

    public CrocodileEggBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HATCH, 0).with(EGGS, 1));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(EGGS)) {
            case 1:
            default:
                return ONE_EGG_SHAPE;
            case 2:
                return TWO_EGG_SHAPE;
            case 3:
                return THREE_EGG_SHAPE;
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects()) {
            this.tryBreakEgg(world, state, pos, entity, 100);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!(entity instanceof ZombieEntity)) {
            this.tryBreakEgg(world, state, pos, entity, 3);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    private void tryBreakEgg(World world, BlockState state, BlockPos pos, Entity entity, int inverseChance) {
        if (this.breaksEgg(world, entity)) {
            if (!world.isClient && world.random.nextInt(inverseChance) == 0) {
                this.breakEgg(world, pos, state);
            }
        }
    }

    private boolean breaksEgg(World world, Entity entity) {
        if (!(entity instanceof CrocodileEntity) && !(entity instanceof BatEntity)) {
            if (!(entity instanceof LivingEntity)) {
                return false;
            } else {
                return entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
            }
        } else {
            return false;
        }
    }

    private void breakEgg(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = state.get(EGGS);
        if (i <= 1) {
            world.breakBlock(pos, false);
        } else {
            world.setBlockState(pos, state.with(EGGS, i - 1), 2);
            world.emitGameEvent(null, GameEvent.BLOCK_DESTROY, pos);
            world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
        }
    }

    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        this.breakEgg(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(world.getBlockState(pos).getBlock().asItem())) {
            int currentCount = state.get(EGGS);

            if (currentCount < 3) {
                world.setBlockState(pos, state.with(EGGS, currentCount + 1), 3);

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
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress(world) && isHatchableBelow(world, pos)) {
            int i = state.get(HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.setBlockState(pos, state.with(HATCH, i + 1), 2);
            } else {
                world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.removeBlock(pos, false);
                for (int j = 0; j < state.get(EGGS); ++j) {
                    EntityType<?> crocodile = ModEntities.CROCODILE;
                    LivingEntity crocodileEntity = (LivingEntity) crocodile.create(world);
                    if (crocodileEntity instanceof CrocodileEntity crocodileInstance) {
                        crocodileInstance.setBaby(true);
                            if (world.getRandom().nextDouble() < 0.05) {
                                crocodileInstance.setVariant(CrocodileVariants.ALBINO);
                            } else {
                                crocodileInstance.setVariant(CrocodileVariants.NORMAL);
                            }
                        crocodileEntity.refreshPositionAndAngles(pos.getX() + 0.3 + j * 0.2, pos.getY(), pos.getZ() + 0.3, 0.0F, 0.0F);
                        world.spawnEntity(crocodileEntity);
                    }
                }
            }
        }
    }

    private boolean shouldHatchProgress(World world) {
        float f = world.getSkyAngle(1.0F);
        return (double)f < 0.69 && (double)f > 0.65 || world.random.nextInt(500) == 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HATCH, EGGS);
    }

    static {
        HATCH = Properties.HATCH;
    }

    public static boolean isHatchableBelow(BlockView world, BlockPos pos) {
        return isHatchable(world, pos.down());
    }

    public static boolean isHatchable(BlockView world, BlockPos pos) {
        return world.getBlockState(pos).isIn(ModTags.Blocks.CROCODILE_EGG_HATCHABLE);//world.getBlockState(pos).isOf(Blocks.MUD);
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (isHatchableBelow(world, pos) && !world.isClient) {
            world.syncWorldEvent(2005, pos, 0);
        }
    }
}
package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.PenguinEntity;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PenguinEggBlock extends Block {
    public static final IntProperty HATCH = Properties.HATCH;
    private static final VoxelShape EGG_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 4.0, 9.5);

    public PenguinEggBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HATCH, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return EGG_SHAPE;
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects()) {
            tryBreakEgg(world, pos, entity, 100);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!(entity instanceof ZombieEntity)) {
            tryBreakEgg(world, pos, entity, 3);
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    private void tryBreakEgg(World world, BlockPos pos, Entity entity, int inverseChance) {
        if (breaksEgg(world, entity) && !world.isClient && world.random.nextInt(inverseChance) == 0) {
            breakEgg(world, pos);
        }
    }

    private boolean breaksEgg(World world, Entity entity) {
        if (entity instanceof PenguinEntity || entity instanceof BatEntity) {
            return false;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        return entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }

    private void breakEgg(World world, BlockPos pos) {
        world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_TURTLE_EGG_BREAK,
                SoundCategory.BLOCKS,
                0.7F,
                0.9F + world.random.nextFloat() * 0.2F
        );
        world.breakBlock(pos, false);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        breakEgg(world, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!shouldHatchProgress(world) || !isHatchableBelow(world, pos)) {
            return;
        }

        int hatch = state.get(HATCH);
        if (hatch < 2) {
            world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK,
                    SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            world.setBlockState(pos, state.with(HATCH, hatch + 1), 2);
        } else {
            world.playSound(null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH,
                    SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);

            world.removeBlock(pos, false);

            PenguinEntity penguin = ModEntities.PENGUIN.create(world);
            if (penguin != null) {
                penguin.setBaby(true);
                penguin.refreshPositionAndAngles(
                        pos.getX() + 0.5,
                        pos.getY(),
                        pos.getZ() + 0.5,
                        0.0F,
                        0.0F
                );
                world.spawnEntity(penguin);
            }
        }
    }

    private boolean shouldHatchProgress(World world) {
        float skyAngle = world.getSkyAngle(1.0F);
        return (skyAngle > 0.65F && skyAngle < 0.69F) || world.random.nextInt(500) == 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HATCH);
    }

    public static boolean isHatchableBelow(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(ModTags.Blocks.PENGUIN_EGG_HATCHABLE);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos,
                             BlockState oldState, boolean notify) {
        if (!world.isClient && isHatchableBelow(world, pos)) {
            world.syncWorldEvent(2005, pos, 0);
        }
    }
}
package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RichFarmlandBlock extends FarmlandBlock {

    public RichFarmlandBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(MOISTURE, 0));
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!world.isClient && world.random.nextFloat() < fallDistance - 0.5F && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
            setToRichDirt(entity, state, world, pos);
        }
        entity.handleFallDamage(fallDistance, 1.0F, world.getDamageSources().fall());
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            setToRichDirt(null, state, world, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = state.get(MOISTURE);
        if (!isWaterNearby(world, pos) && !world.hasRain(pos.up())) {
            if (i > 0) {
                world.setBlockState(pos, state.with(MOISTURE, i - 1), 2);
            } else if (!hasCrop(world, pos)) {
                setToRichDirt(null, state, world, pos);
            }
        } else if (i < 7) {
            world.setBlockState(pos, state.with(MOISTURE, 7), 2);
        }

        BlockPos cropPos = pos.up();
        BlockState cropState = world.getBlockState(cropPos);
        if (cropState.getBlock() instanceof Fertilizable fertilizable) {
            if (random.nextFloat() < 0.33f) {
                if (fertilizable.isFertilizable(world, cropPos, cropState)) {
                    fertilizable.grow(world, random, cropPos, cropState);
                }
            }
        }
    }

    public static void setToRichDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        BlockState richDirtState = pushEntitiesUpBeforeBlockChange(state, ModBlocks.RICH_DIRT.getDefaultState(), world, pos);
        world.setBlockState(pos, richDirtState);
    }

    private static boolean hasCrop(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.up()).isIn(net.minecraft.registry.tag.BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isWaterNearby(WorldView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate(pos.add(-4, 0, -4), pos.add(4, 1, 4))) {
            if (world.getFluidState(blockPos).isIn(net.minecraft.registry.tag.FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }
}

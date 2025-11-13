package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class UnicornDustItem extends Item {

    public UnicornDustItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        if (!(state.isOf(Blocks.GRASS_BLOCK))) {
            if (growFertilizable(stack, world, pos)) {
                spawnParticlesAndSound((ServerWorld) world, pos, player);
                return ActionResult.SUCCESS;
            }
        }

        boolean used = false;

        if (state.isOf(Blocks.DIRT) || state.isOf(Blocks.MYCELIUM)
                || state.isOf(Blocks.PODZOL) || state.isOf(Blocks.COARSE_DIRT) || state.isOf(Blocks.MUD)) {
            createGrassPatch((ServerWorld) world, pos);
            used = true;
        }

        else if (state.isOf(Blocks.GRASS_BLOCK)) {
            BlockPos above = pos.up();
            if (world.isAir(above) && useOnGround(stack, world, above)) {
                used = true;
            }
        }

        if (used) {
            spawnParticlesAndSound((ServerWorld) world, pos, player);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private void createGrassPatch(ServerWorld world, BlockPos center) {
        Random random = world.getRandom();
        int radius = 2 + random.nextInt(2);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (random.nextFloat() < 0.8F) {
                    BlockPos pos = center.add(dx, 0, dz);
                    BlockState state = world.getBlockState(pos);

                    if (state.isOf(Blocks.DIRT) || state.isOf(Blocks.COARSE_DIRT)
                            || state.isOf(Blocks.MUD) || state.isOf(Blocks.PODZOL)
                            || state.isOf(Blocks.MYCELIUM)) {
                        world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 3);

                        if (world.isAir(pos.up()) && random.nextFloat() < 0.35F) {
                            float r = random.nextFloat();
                            if (r < 0.55F) {
                                world.setBlockState(pos.up(), Blocks.SHORT_GRASS.getDefaultState(), 3);
                            } else if (r < 0.65F) {
                                placeTallGrass(world, pos.up());
                            } else {
                                placeFlowers(world, pos.up(), random);
                            }
                        }
                    }
                }
            }
        }
    }

    private void placeTallGrass(ServerWorld world, BlockPos pos) {
        TallPlantBlock.placeAt(world, Blocks.TALL_GRASS.getDefaultState(), pos, 3);
    }

    private void spawnParticlesAndSound(ServerWorld world, BlockPos pos, PlayerEntity player) {
        spawnSparkleParticles(world, pos);
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.8F, 1.2F);
        if (player != null) {
            player.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }
    }

    private boolean growFertilizable(ItemStack stack, World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.getBlock() instanceof Fertilizable fertilizable) {
            if (fertilizable.isFertilizable(world, pos, blockState)) {
                if (world instanceof ServerWorld serverWorld) {
                    if (fertilizable.canGrow(world, serverWorld.random, pos, blockState)) {
                        fertilizable.grow(serverWorld, serverWorld.random, pos, blockState);
                    }
                    stack.decrement(1);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean useOnGround(ItemStack stack, World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return false;

        Random random = world.getRandom();
        boolean used = false;

        for (int i = 0; i < 64; i++) {
            BlockPos target = pos.add(random.nextInt(8) - 4, random.nextInt(4) - 2, random.nextInt(8) - 4);
            BlockState targetState = serverWorld.getBlockState(target);

            if (targetState.isOf(Blocks.GRASS_BLOCK) && serverWorld.isAir(target.up())) {
                float r = random.nextFloat();
                if (r < 0.55F) {
                    serverWorld.setBlockState(target.up(), Blocks.SHORT_GRASS.getDefaultState(), 3);
                } else if (r < 0.65F) {
                    placeTallGrass(serverWorld, target.up());
                } else {
                    placeFlowers(serverWorld, target.up(), random);
                }
                used = true;
            }
        }

        if (used) {
            stack.decrement(1);
        }
        return used;
    }

    private void placeFlowers(ServerWorld world, BlockPos pos, Random random) {
        BlockState[] customFlowers = new BlockState[]{
                ModBlocks.FIREWEED_BUSH.getDefaultState(),
                ModBlocks.BLUE_SAGE.getDefaultState(),
                ModBlocks.CRIMSON_BLOOM.getDefaultState()
        };

        BlockState chosen = customFlowers[random.nextInt(customFlowers.length)];
        world.setBlockState(pos, chosen, 3);
    }

    private void spawnSparkleParticles(ServerWorld world, BlockPos pos) {
        world.spawnParticles(
                ModParticles.UNICORN_DUST,
                pos.getX() + 0.5,
                pos.getY() + 1.0,
                pos.getZ() + 0.5,
                30, 0.5, 0.5, 0.5, 0.0
        );
    }
}
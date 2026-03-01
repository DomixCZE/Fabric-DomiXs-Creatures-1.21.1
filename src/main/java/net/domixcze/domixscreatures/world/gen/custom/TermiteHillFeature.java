package net.domixcze.domixscreatures.world.gen.custom;

import com.mojang.serialization.Codec;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.entity.TermiteNestBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class TermiteHillFeature extends Feature<DefaultFeatureConfig> {
    public TermiteHillFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos origin = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        if (!world.getBlockState(origin.down()).isIn(BlockTags.DIRT)) {
            return false;
        }

        int splashRadius = 5;
        for (int x = -splashRadius; x <= splashRadius; x++) {
            for (int z = -splashRadius; z <= splashRadius; z++) {
                double distance = Math.sqrt(x * x + z * z);

                if (distance <= splashRadius) {
                    float chance = 1.0f - (float)(distance / splashRadius);

                    BlockPos groundPos = origin.add(x, -1, z);
                    BlockState currentState = world.getBlockState(groundPos);

                    if (currentState.isIn(BlockTags.DIRT) && random.nextFloat() < (chance * 0.8f)) {
                        BlockState blendState = (random.nextFloat() < 0.7f)
                                ? Blocks.PACKED_MUD.getDefaultState()
                                : ModBlocks.TERMITE_MOUND.getDefaultState();

                        world.setBlockState(groundPos, blendState, 3);
                    }
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos columnBase = origin.add(x, 0, z);
                int columnHeight;

                if (x == 0 && z == 0) columnHeight = 5 + random.nextInt(3);
                else if (Math.abs(x) + Math.abs(z) == 1) columnHeight = 2 + random.nextInt(2);
                else columnHeight = random.nextInt(3);

                for (int y = 0; y < columnHeight; y++) {
                    BlockPos placePos = columnBase.up(y);
                    if (world.getBlockState(placePos).isReplaceable()) {
                        if (x == 0 && z == 0 && y == 0) {
                            world.setBlockState(placePos, ModBlocks.TERMITE_NEST_BLOCK.getDefaultState(), 3);

                            BlockEntity be = world.getBlockEntity(placePos);
                            if (be instanceof TermiteNestBlockEntity nest) {
                                nest.setHasQueen(true);
                                nest.markDirty();
                            }
                        } else {
                            world.setBlockState(placePos, ModBlocks.TERMITE_MOUND.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
        return true;
    }
}
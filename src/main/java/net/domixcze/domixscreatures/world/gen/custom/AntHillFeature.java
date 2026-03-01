package net.domixcze.domixscreatures.world.gen.custom;

import com.mojang.serialization.Codec;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class AntHillFeature extends Feature<DefaultFeatureConfig> {
    public AntHillFeature(Codec<DefaultFeatureConfig> configCodec) {
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

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos pos = origin.add(x, 0, z);

                if (x == 0 && z == 0) {
                    world.setBlockState(pos, ModBlocks.ANT_NEST_BLOCK.getDefaultState(), 3);

                    BlockEntity be = world.getBlockEntity(pos);
                    if (be instanceof AbstractColonyBlockEntity colony) {
                        colony.setHasQueen(true);
                        colony.markDirty();
                    }

                    world.setBlockState(pos.up(), ModBlocks.ANT_MOUND.getDefaultState(), 3);
                }
                else if (Math.abs(x) + Math.abs(z) == 1) {
                    world.setBlockState(pos, ModBlocks.ANT_MOUND.getDefaultState(), 3);
                }
                else {
                    if (random.nextFloat() < 0.5f) {
                        world.setBlockState(pos, ModBlocks.ANT_MOUND.getDefaultState(), 3);
                    }
                }
            }
        }

        return true;
    }
}
package net.domixcze.domixscreatures.world.gen.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class HippoMudBathFeature extends Feature<DefaultFeatureConfig> {

    public HippoMudBathFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos origin = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        Random random = context.getRandom();

        int centerX = origin.getX();
        int centerZ = origin.getZ();

        int centerY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, centerX, centerZ);
        if (!world.getFluidState(origin.withY(centerY - 1)).isEmpty()) return false;

        int checkDist = 7;
        if (Math.abs(centerY - world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, centerX + checkDist, centerZ)) > 3 ||
                Math.abs(centerY - world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, centerX - checkDist, centerZ)) > 3 ||
                Math.abs(centerY - world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, centerX, centerZ + checkDist)) > 3 ||
                Math.abs(centerY - world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, centerX, centerZ - checkDist)) > 3) {
            return false;
        }

        if (!world.getBlockState(origin.withY(centerY - 1)).isIn(BlockTags.DIRT)) return false;

        final float holeRadius = 3.5f;
        final float packedRadius = 9.0f;
        final int maxRadius = 10;
        final int maxRadiusSq = maxRadius * maxRadius;

        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        BlockPos.Mutable floorPos = new BlockPos.Mutable();

        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                int distSq = x * x + z * z;
                if (distSq > maxRadiusSq) continue;

                int targetX = centerX + x;
                int targetZ = centerZ + z;
                int topY = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, targetX, targetZ);

                mutablePos.set(targetX, topY - 1, targetZ);

                BlockState state = world.getBlockState(mutablePos);
                if (!state.isIn(BlockTags.DIRT)) {
                    if (state.isAir() || state.isIn(BlockTags.REPLACEABLE)) {
                        int scanLimit = 0;
                        while (scanLimit < 4 && mutablePos.getY() > world.getBottomY() && !world.getBlockState(mutablePos).isIn(BlockTags.DIRT)) {
                            mutablePos.move(0, -1, 0);
                            scanLimit++;
                        }
                    }
                    if (!world.getBlockState(mutablePos).isIn(BlockTags.DIRT)) continue;
                }

                float dist = (float) Math.sqrt(distSq);
                float effectiveDist = dist + (random.nextFloat() - 0.5f) * 1.2f;

                if (effectiveDist < holeRadius) {
                    carveHole(world, mutablePos, floorPos);
                } else if (effectiveDist < packedRadius) {
                    applyMudTransition(world, random, mutablePos, floorPos, dist, effectiveDist);
                }
            }
        }
        return true;
    }

    private void carveHole(StructureWorldAccess world, BlockPos surfacePos, BlockPos.Mutable floorPos) {
        world.setBlockState(surfacePos, Blocks.AIR.getDefaultState(), 3);
        floorPos.set(surfacePos).move(0, -1, 0);
        if (world.getBlockState(floorPos).isIn(BlockTags.DIRT)) {
            world.setBlockState(floorPos, Blocks.MUD.getDefaultState(), 3);
        }
    }

    private void applyMudTransition(StructureWorldAccess world, Random random, BlockPos pos, BlockPos.Mutable floorPos,
                                    float dist, float effectiveDist) {
        if (dist < (float) 4.7) {
            placeDeepMud(world, pos, floorPos);
            return;
        }

        float falloff = (effectiveDist - (float) 3.5) / ((float) 9.0 - (float) 3.5);
        if (random.nextFloat() < (1.0f - falloff * falloff)) {
            float mudWeight = Math.max(0, 1.0f - (effectiveDist - (float) 3.5) / ((float) 6.0 - (float) 3.5));

            if (random.nextFloat() < mudWeight * 0.9f) {
                placeDeepMud(world, pos, floorPos);
            } else {
                world.setBlockState(pos, Blocks.PACKED_MUD.getDefaultState(), 3);
            }
        }
    }

    private void placeDeepMud(StructureWorldAccess world, BlockPos pos, BlockPos.Mutable floorPos) {
        world.setBlockState(pos, Blocks.MUD.getDefaultState(), 3);
        floorPos.set(pos).move(0, -1, 0);
        if (world.getBlockState(floorPos).isIn(BlockTags.DIRT)) {
            world.setBlockState(floorPos, Blocks.MUD.getDefaultState(), 3);
        }
    }
}

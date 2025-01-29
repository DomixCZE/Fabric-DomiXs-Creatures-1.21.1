package net.domixcze.domixscreatures.world.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.world.tree.ModFoliagePlacerTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class SpectralFoliagePlacer extends FoliagePlacer {
    public static final Codec<SpectralFoliagePlacer> CODEC = RecordCodecBuilder.create(spectralFoliagePlacerInstance ->
            fillFoliagePlacerFields(spectralFoliagePlacerInstance).and(Codec.intRange(0, 12).fieldOf("height")
                    .forGetter(Instance -> Instance.height)).apply(spectralFoliagePlacerInstance, SpectralFoliagePlacer::new));

    private final int height;

    public SpectralFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
        super(radius, offset);
        this.height = height;
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return ModFoliagePlacerTypes.SPECTRAL_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, int trunkHeight, TreeNode treeNode, int foliageHeight, int radius, int offset) {
        BlockPos center = treeNode.getCenter();

        // Generate main blob foliage
        for (int y = offset; y >= offset - foliageHeight; --y) {
            int currentRadius = Math.max(radius + treeNode.getFoliageRadius() - 1 - y / 2, 0);
            this.generateSquare(world, placer, random, config, center, currentRadius, y, treeNode.isGiantTrunk());
        }

        // Add hanging leaves
        for (int y = offset - foliageHeight; y <= offset; ++y) {
            int currentRadius = Math.max(radius + treeNode.getFoliageRadius() - 1 - y / 2, 0);

            for (int dx = -currentRadius; dx <= currentRadius; ++dx) {
                for (int dz = -currentRadius; dz <= currentRadius; ++dz) {
                    if (random.nextFloat() < 0.3F) { // Chance for hanging leaves
                        for (int dy = 1; dy <= 2; ++dy) { // Extend downwards by 1-2 blocks
                            BlockPos hangingPos = center.add(dx, y - dy, dz);
                            placeLeavesBlock(world, placer, random, config, hangingPos);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return this.height;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return dx == radius && dz == radius && (random.nextInt(2) == 0 || y == 0);
    }

    private void placeLeavesBlock(TestableWorld world, BlockPlacer placer, Random random, TreeFeatureConfig config, BlockPos pos) {
        if (world.testBlockState(pos, state -> state.isAir())) {
            placer.placeBlock(pos, config.foliageProvider.get(random, pos));
        }
    }
}
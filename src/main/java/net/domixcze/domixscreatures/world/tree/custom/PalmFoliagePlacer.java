package net.domixcze.domixscreatures.world.tree.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.world.tree.ModFoliagePlacerTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class PalmFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<PalmFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            fillFoliagePlacerFields(instance)
                    .apply(instance, PalmFoliagePlacer::new));

    public PalmFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> getType() {
        return ModFoliagePlacerTypes.PALM_FOLIAGE_PLACER;
    }

    @Override
    protected void generate(TestableWorld world, BlockPlacer placer, Random random,
                            TreeFeatureConfig config, int trunkHeight,
                            TreeNode treeNode, int foliageHeight, int radius, int offset) {

        BlockPos center = treeNode.getCenter();

        // 5x5 leaf canopy
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos leafPos = center.add(dx, 0, dz);
                placeLeaf(world, placer, random, config, leafPos);

                // Hanging corners
                if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
                    placeLeaf(world, placer, random, config, leafPos.down());
                }
            }
        }

        int[][] sideOffsets = {
                {0, 3},   // South
                {0, -3},  // North
                {3, 0},   // East
                {-3, 0}   // West
        };

        for (int[] offsetXZ : sideOffsets) {
            BlockPos first = center.add(offsetXZ[0], 0, offsetXZ[1]);
            BlockPos second = first.down();
            BlockPos third = second.down();

            placeLeaf(world, placer, random, config, first);
            placeLeaf(world, placer, random, config, second);
            placeLeaf(world, placer, random, config, third);
        }

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                if (Math.abs(dx) + Math.abs(dz) <= 2 && (dx != 0 || dz != 0)) {
                    BlockPos topLeaf = center.add(dx, 1, dz);
                    placeLeaf(world, placer, random, config, topLeaf);
                    placeLeaf(world, placer, random, config, center.up());
                }
            }
        }
    }

    private void placeLeaf(TestableWorld world, BlockPlacer placer, Random random,
                           TreeFeatureConfig config, BlockPos pos) {
        if (world.testBlockState(pos, state -> state.isAir())) {
            placer.placeBlock(pos, config.foliageProvider.get(random, pos));
        }
    }

    @Override
    public int getRandomHeight(Random random, int trunkHeight, TreeFeatureConfig config) {
        return 0;
    }

    @Override
    protected boolean isInvalidForLeaves(Random random, int dx, int y, int dz, int radius, boolean giantTrunk) {
        return false;
    }
}
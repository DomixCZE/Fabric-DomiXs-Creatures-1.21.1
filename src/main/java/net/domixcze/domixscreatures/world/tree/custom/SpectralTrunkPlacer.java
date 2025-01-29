package net.domixcze.domixscreatures.world.tree.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.world.tree.ModTrunkPlacerTypes;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SpectralTrunkPlacer extends TrunkPlacer {
    public static final Codec<SpectralTrunkPlacer> CODEC = RecordCodecBuilder.create(objectInstance ->
            fillTrunkPlacerFields(objectInstance).apply(objectInstance, SpectralTrunkPlacer::new));

    public SpectralTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return ModTrunkPlacerTypes.SPECTRAL_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, int height, BlockPos startPos, TreeFeatureConfig config) {
        List<FoliagePlacer.TreeNode> foliageNodes = new ArrayList<>();

        // Trunk height
        height = Math.min(height, random.nextInt(2) + 4);

        // Generate the trunk
        for (int y = 0; y < height; y++) {
            BlockPos currentPos = startPos.up(y);
            placeLog(replacer, world, currentPos, Direction.Axis.Y, config, random);
        }

        // Generate branches at the top of the trunk
        BlockPos topPos = startPos.up(height - 1);

        // Ensure that one main branch is always generated on each side of the trunk (North, South, East, West)
        Direction[] mainBranchDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for (Direction direction : mainBranchDirections) {
            generateMainBranch(world, replacer, random, topPos, direction, config, foliageNodes);
        }

        // Extend the trunk upwards by 7 more blocks after the main branches
        BlockPos extendedTrunkPos = topPos.up(5);
        for (int y = 0; y < 7; y++) { // 7-block extension
            BlockPos trunkPos = extendedTrunkPos.down(6 - y);
            placeLog(replacer, world, trunkPos, Direction.Axis.Y, config, random);
        }

        // Add foliage at the top of the extended trunk
        foliageNodes.add(new FoliagePlacer.TreeNode(extendedTrunkPos, 0, false));

        // Generate the small side branches 2 blocks before the end of the extended trunk
        BlockPos branchBasePos = extendedTrunkPos.down(2);
        Direction[] sideBranchDirections = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (Direction direction : sideBranchDirections) {
            generateSideBranch(world, replacer, random, branchBasePos, direction, config, foliageNodes);
        }

        return foliageNodes;
    }

    private void generateMainBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, Direction direction, TreeFeatureConfig config, List<FoliagePlacer.TreeNode> foliageNodes) {
        int branchLength = random.nextInt(2) + 3;
        BlockPos branchPos = startPos;

        for (int i = 0; i < branchLength; i++) {
            branchPos = branchPos.offset(direction);

            if (i % 2 == 0) {
                branchPos = branchPos.up(1);
            }

            placeLog(replacer, world, branchPos, direction.getAxis(), config, random);

            if (i > 1 && random.nextFloat() < 0.5f) { // chance for a sub-branch
                generateSubBranch(world, replacer, random, branchPos, direction, config, foliageNodes);
            }

            if (i == branchLength - 1) {
                foliageNodes.add(new FoliagePlacer.TreeNode(branchPos, 0, false));
            }
        }
    }

    private void generateSubBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, Direction parentDirection, TreeFeatureConfig config, List<FoliagePlacer.TreeNode> foliageNodes) {
        int subBranchLength = random.nextInt(2) + 2; // Sub-branch length
        Direction direction = parentDirection.rotateYClockwise();
        BlockPos branchPos = startPos;

        for (int i = 0; i < subBranchLength; i++) {
            branchPos = branchPos.offset(direction);

            if (i % 2 == 0) {
                branchPos = branchPos.up(1);
            }

            placeLog(replacer, world, branchPos, direction.getAxis(), config, random);

            if (i == subBranchLength - 1) {
                foliageNodes.add(new FoliagePlacer.TreeNode(branchPos, 0, false));
            }
        }
    }

    private void generateSideBranch(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, BlockPos startPos, Direction direction, TreeFeatureConfig config, List<FoliagePlacer.TreeNode> foliageNodes) {
        int sideBranchLength = 2; // Fixed side branch length of 2
        BlockPos branchPos = startPos;

        for (int i = 0; i < sideBranchLength; i++) {
            branchPos = branchPos.offset(direction);

            placeLog(replacer, world, branchPos, direction.getAxis(), config, random);

            if (i == sideBranchLength - 1) {
                foliageNodes.add(new FoliagePlacer.TreeNode(branchPos, 0, false));
            }
        }
    }

    private void placeLog(BiConsumer<BlockPos, BlockState> replacer, TestableWorld world, BlockPos pos, Direction.Axis axis, TreeFeatureConfig config, Random random) {
        if (isAir(world, pos)) {
            replacer.accept(pos, config.trunkProvider.get(random, pos).with(Properties.AXIS, axis));
        }
    }

    private boolean isAir(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, BlockState::isAir);
    }
}
package net.domixcze.domixscreatures.world.tree.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.CoconutBlock;
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

public class PalmTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<PalmTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            fillTrunkPlacerFields(instance).apply(instance, PalmTrunkPlacer::new));

    public PalmTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
    }

    @Override
    protected TrunkPlacerType<?> getType() {
        return ModTrunkPlacerTypes.PALM_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.TreeNode> generate(TestableWorld world,
                                                 BiConsumer<BlockPos, BlockState> replacer,
                                                 Random random,
                                                 int height,
                                                 BlockPos startPos,
                                                 TreeFeatureConfig config) {
        List<FoliagePlacer.TreeNode> foliageNodes = new ArrayList<>();

        Direction lean = Direction.Type.HORIZONTAL.random(random); // Random direction for lean
        BlockPos pos = startPos;

        int firstPillarHeight = 3 + random.nextInt(3); // 3 to 5
        for (int i = 0; i < firstPillarHeight; i++) {
            placeLog(replacer, world, pos, Direction.Axis.Y, config, random);
            pos = pos.up();
        }

        BlockPos sidePillarBase = pos.down().offset(lean);
        BlockPos leafTop = sidePillarBase;

        for (int i = 0; i < 5; i++) {
            placeLog(replacer, world, leafTop, Direction.Axis.Y, config, random);
            leafTop = leafTop.up();
        }

        BlockPos coconutAnchor = leafTop.down(2);
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (random.nextBoolean()) {
                BlockPos coconutPos = coconutAnchor.offset(dir);
                if (world.testBlockState(coconutPos, state -> state.isAir())) {
                    BlockState coconut = ModBlocks.COCONUT_BLOCK.getDefaultState()
                            .with(CoconutBlock.FACING, dir.getOpposite())
                            .with(CoconutBlock.ATTACHED, true);
                    replacer.accept(coconutPos, coconut);
                }
            }
        }

        foliageNodes.add(new FoliagePlacer.TreeNode(leafTop.down(), 0, false));
        return foliageNodes;
    }

    private void placeLog(BiConsumer<BlockPos, BlockState> replacer, TestableWorld world,
                          BlockPos pos, Direction.Axis axis, TreeFeatureConfig config, Random random) {
        if (world.testBlockState(pos, BlockState::isAir)) {
            replacer.accept(pos, config.trunkProvider.get(random, pos).with(Properties.AXIS, axis));
        }
    }
}
package net.domixcze.domixscreatures.world;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.world.tree.custom.PalmFoliagePlacer;
import net.domixcze.domixscreatures.world.tree.custom.PalmTrunkPlacer;
import net.domixcze.domixscreatures.world.tree.custom.SpectralFoliagePlacer;
import net.domixcze.domixscreatures.world.tree.custom.SpectralTrunkPlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerbedBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SPECTRAL_KEY = registerKey("spectral");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");
    public static final RegistryKey<ConfiguredFeature<?, ?>> MAGNETITE_ORE_KEY = registerKey("magnetite_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> JADE_ORE_KEY = registerKey("jade_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> LIMESTONE_KEY = registerKey("limestone");
    public static final RegistryKey<ConfiguredFeature<?, ?>> CLOVERS_KEY = registerKey("clovers");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        // Spectral tree
        register(context, SPECTRAL_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.SPECTRAL_LOG),
                new SpectralTrunkPlacer(5, 4, 3),

                BlockStateProvider.of(ModBlocks.SPECTRAL_LEAVES),
                new SpectralFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1), 2),

                new TwoLayersFeatureSize(1, 0, 2)).dirtProvider(BlockStateProvider.of(Blocks.END_STONE)).build());

        // Palm tree
        register(context, PALM_KEY, Feature.TREE, new TreeFeatureConfig.Builder(
                BlockStateProvider.of(ModBlocks.PALM_LOG),
                new PalmTrunkPlacer(0, 0, 0),

                BlockStateProvider.of(ModBlocks.PALM_LEAVES),
                new PalmFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0)),

                new TwoLayersFeatureSize(1, 0, 1)).dirtProvider(BlockStateProvider.of(Blocks.SAND)).build());

        // Clovers
        register(context, CLOVERS_KEY, Feature.FLOWER, new RandomPatchFeatureConfig(
                48,
                6,
                2,
                PlacedFeatures.createEntry(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockFeatureConfig(
                                new WeightedBlockStateProvider(
                                        DataPool.<BlockState>builder()
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.NORTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 4), 6)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.NORTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 3), 5)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.NORTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 2), 2)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.NORTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 1), 1)

                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.EAST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 4), 6)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.EAST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 3), 5)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.EAST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 2), 2)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.EAST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 1), 1)

                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.SOUTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 4), 6)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.SOUTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 3), 5)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.SOUTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 2), 2)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.SOUTH)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 1), 1)

                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.WEST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 4), 6)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.WEST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 3), 5)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.WEST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 2), 2)
                                                .add(ModBlocks.CLOVERS.getDefaultState()
                                                        .with(FlowerbedBlock.FACING, Direction.WEST)
                                                        .with(FlowerbedBlock.FLOWER_AMOUNT, 1), 1)
                                                .build()
                                )
                        )
                )
        ));

        // Magnetite ore
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldMagnetiteOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.MAGNETITE_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_MAGNETITE_ORE.getDefaultState()));

        register(context, MAGNETITE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldMagnetiteOres, 10));

        // Jade ore
        List<OreFeatureConfig.Target> overworldJadeOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.JADE_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_JADE_ORE.getDefaultState()));

        register(context, JADE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldJadeOres, 5));

        // Limestone
        RuleTest baseStone = new TagMatchRuleTest(BlockTags.BASE_STONE_OVERWORLD);
        List<OreFeatureConfig.Target> limestoneTargets =
                List.of(OreFeatureConfig.createTarget(baseStone, ModBlocks.LIMESTONE.getDefaultState()));

        register(context, LIMESTONE_KEY, Feature.ORE, new OreFeatureConfig(limestoneTargets, 32));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(DomiXsCreatures.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
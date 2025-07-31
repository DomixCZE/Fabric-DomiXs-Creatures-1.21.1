package net.domixcze.domixscreatures.world;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.world.tree.custom.PalmFoliagePlacer;
import net.domixcze.domixscreatures.world.tree.custom.PalmTrunkPlacer;
import net.domixcze.domixscreatures.world.tree.custom.SpectralFoliagePlacer;
import net.domixcze.domixscreatures.world.tree.custom.SpectralTrunkPlacer;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SPECTRAL_KEY = registerKey("spectral");
    public static final RegistryKey<ConfiguredFeature<?, ?>> PALM_KEY = registerKey("palm");
    public static final RegistryKey<ConfiguredFeature<?, ?>> MAGNETITE_ORE_KEY = registerKey("magnetite_ore");

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

        // Magnetite ore
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreFeatureConfig.Target> overworldMagnetiteOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, ModBlocks.MAGNETITE_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, ModBlocks.DEEPSLATE_MAGNETITE_ORE.getDefaultState()));

        register(context, MAGNETITE_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldMagnetiteOres, 10));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(DomiXsCreatures.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
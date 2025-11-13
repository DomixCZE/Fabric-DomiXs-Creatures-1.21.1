package net.domixcze.domixscreatures.world;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final RegistryKey<PlacedFeature> PALM_PLACED_KEY = registerKey("palm_placed");
    public static final RegistryKey<PlacedFeature> MAGNETITE_ORE_KEY = registerKey("magnetite_ore_placed");
    public static final RegistryKey<PlacedFeature> JADE_ORE_KEY = registerKey("jade_ore_placed");
    public static final RegistryKey<PlacedFeature> LIMESTONE_PLACED_KEY = registerKey("limestone_placed");
    public static final RegistryKey<PlacedFeature> CLOVERS_PLACED_KEY = registerKey("clovers_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, PALM_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PALM_KEY),
                List.of(
                        RarityFilterPlacementModifier.of(3),
                        SquarePlacementModifier.of(),
                        PlacedFeatures.WORLD_SURFACE_WG_HEIGHTMAP,
                        BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(ModBlocks.PALM_SAPLING.getDefaultState(), BlockPos.ORIGIN)),
                        BiomePlacementModifier.of()
                )
        );

        register(context, CLOVERS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CLOVERS_KEY),
                List.of(
                        NoiseThresholdCountPlacementModifier.of(-0.8D, 2, 5),
                        SquarePlacementModifier.of(),
                        PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
                        BiomePlacementModifier.of()
                ));

        register(context, MAGNETITE_ORE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.MAGNETITE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(7,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-50), YOffset.fixed(50))
                        ));

        register(context, JADE_ORE_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.JADE_ORE_KEY),
                ModOrePlacement.modifiersWithCount(5,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-30), YOffset.fixed(30))
                ));

        register(context, LIMESTONE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LIMESTONE_KEY),
                ModOrePlacement.modifiersWithCount(4,
                        HeightRangePlacementModifier.uniform(YOffset.fixed(0), YOffset.fixed(50))
                ));
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(DomiXsCreatures.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}

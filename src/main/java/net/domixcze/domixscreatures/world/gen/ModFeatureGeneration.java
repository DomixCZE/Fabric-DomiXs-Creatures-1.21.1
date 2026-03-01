package net.domixcze.domixscreatures.world.gen;

import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.world.ModPlacedFeatures;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;

public class ModFeatureGeneration {
    public static void generateFeatures() {
        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.HAS_TERMITE_HILL),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                ModPlacedFeatures.TERMITE_HILL_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.HAS_ANT_HILL),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                ModPlacedFeatures.ANT_HILL_PLACED_KEY);

        BiomeModifications.addFeature(BiomeSelectors.tag(ModTags.Biomes.HAS_HIPPO_MUD_BATH),
                GenerationStep.Feature.SURFACE_STRUCTURES,
                ModPlacedFeatures.HIPPO_MUD_BATH_PLACED_KEY);
    }
}
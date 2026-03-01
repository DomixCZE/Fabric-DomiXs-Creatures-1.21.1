package net.domixcze.domixscreatures.world.gen;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.world.gen.custom.AntHillFeature;
import net.domixcze.domixscreatures.world.gen.custom.HippoMudBathFeature;
import net.domixcze.domixscreatures.world.gen.custom.TermiteHillFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class ModFeatures {
    public static final Feature<DefaultFeatureConfig> TERMITE_HILL = register("termite_hill",
            new TermiteHillFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> ANT_HILL = register("ant_hill",
            new AntHillFeature(DefaultFeatureConfig.CODEC));

    public static final Feature<DefaultFeatureConfig> HIPPO_MUD_BATH = register("hippo_mud_bath",
            new HippoMudBathFeature(DefaultFeatureConfig.CODEC));

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return Registry.register(Registries.FEATURE, Identifier.of(DomiXsCreatures.MOD_ID, name), feature);
    }

    public static void registerFeatures() {
        DomiXsCreatures.LOGGER.info("Registering Features for " + DomiXsCreatures.MOD_ID);
    }
}
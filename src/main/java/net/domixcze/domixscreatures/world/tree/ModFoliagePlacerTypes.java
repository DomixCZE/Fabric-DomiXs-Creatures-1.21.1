package net.domixcze.domixscreatures.world.tree;

import com.mojang.serialization.MapCodec;
import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.world.tree.custom.SpectralFoliagePlacer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<SpectralFoliagePlacer> SPECTRAL_FOLIAGE_PLACER = registerFoliagePlacerType("spectral_foliage_placer", SpectralFoliagePlacer.CODEC);

    private static <P extends FoliagePlacer> FoliagePlacerType<P> registerFoliagePlacerType(String id, MapCodec<P> codec) {
        return Registry.register(Registries.FOLIAGE_PLACER_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, id), new FoliagePlacerType<>(codec));
    }

    public static void register() {
        DomiXsCreatures.LOGGER.info("Registering Foliage Placer Types for " + DomiXsCreatures.MOD_ID);
    }
}
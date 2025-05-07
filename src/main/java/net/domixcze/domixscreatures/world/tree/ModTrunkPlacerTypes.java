package net.domixcze.domixscreatures.world.tree;

import com.mojang.serialization.MapCodec;
import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.world.tree.custom.SpectralTrunkPlacer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ModTrunkPlacerTypes {
    public static final TrunkPlacerType<SpectralTrunkPlacer> SPECTRAL_TRUNK_PLACER = registerTrunkPlacerType("spectral_trunk_placer", SpectralTrunkPlacer.CODEC);

    private static <P extends TrunkPlacer> TrunkPlacerType<P> registerTrunkPlacerType(String id, MapCodec<P> codec) {
        return Registry.register(Registries.TRUNK_PLACER_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, id), new TrunkPlacerType<>(codec));
    }

    public static void register() {
        DomiXsCreatures.LOGGER.info("Registering Trunk Placer Types for " + DomiXsCreatures.MOD_ID);
    }
}
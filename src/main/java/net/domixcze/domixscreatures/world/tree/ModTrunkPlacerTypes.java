package net.domixcze.domixscreatures.world.tree;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.mixin.TrunkPlacerTypeInvoker;
import net.domixcze.domixscreatures.world.tree.custom.SpectralTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class ModTrunkPlacerTypes {
    public static final TrunkPlacerType<?> SPECTRAL_TRUNK_PLACER = TrunkPlacerTypeInvoker.callRegister("spectral_trunk_placer", SpectralTrunkPlacer.CODEC);

    public static void register() {
        DomiXsCreatures.LOGGER.info("Registering Trunk Placers for " + DomiXsCreatures.MOD_ID);
    }
}

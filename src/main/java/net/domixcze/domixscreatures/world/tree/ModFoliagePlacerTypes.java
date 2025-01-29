package net.domixcze.domixscreatures.world.tree;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.mixin.FoliagePlacerTypeInvoker;
import net.domixcze.domixscreatures.world.tree.custom.SpectralFoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;

public class ModFoliagePlacerTypes {
    public static final FoliagePlacerType<?> SPECTRAL_FOLIAGE_PLACER = FoliagePlacerTypeInvoker.callRegister("spectral_foliage_placer", SpectralFoliagePlacer.CODEC);

    public static void register() {
        DomiXsCreatures.LOGGER.info("Registering Foliage Placer for " + DomiXsCreatures.MOD_ID);
    }
}

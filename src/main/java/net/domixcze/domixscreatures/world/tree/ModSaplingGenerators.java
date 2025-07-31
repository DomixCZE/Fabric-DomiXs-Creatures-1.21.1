package net.domixcze.domixscreatures.world.tree;

import net.domixcze.domixscreatures.world.ModConfiguredFeatures;
import net.minecraft.block.SaplingGenerator;

import java.util.Optional;

public class ModSaplingGenerators {
    public static final SaplingGenerator SPECTRAL =
            new SaplingGenerator("spectral", 0f, Optional.empty(),
                    Optional.empty(),
                    Optional.of(ModConfiguredFeatures.SPECTRAL_KEY),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty());

    public static final SaplingGenerator PALM =
            new SaplingGenerator("palm", 0f, Optional.empty(),
                    Optional.empty(),
                    Optional.of(ModConfiguredFeatures.PALM_KEY),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty());
}
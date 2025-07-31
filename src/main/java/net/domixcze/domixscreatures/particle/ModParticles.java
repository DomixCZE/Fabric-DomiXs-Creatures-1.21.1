package net.domixcze.domixscreatures.particle;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType SCREECH = register("screech");
    public static final SimpleParticleType INK = register("ink");
    public static final SimpleParticleType SLEEP = register("sleep");
    public static final SimpleParticleType SPECTRAL_LEAVES = register("spectral_leaves");
    public static final SimpleParticleType BLOOD_PUDDLE = register("blood_puddle");
    public static final SimpleParticleType FALLING_BLOOD = register("falling_blood");
    public static final SimpleParticleType ELECTRIC = register("electric");

    public static final SimpleParticleType NEGATIVE_MAGNET = register("negative_magnet");
    public static final SimpleParticleType POSITIVE_MAGNET = register("positive_magnet");

    private static SimpleParticleType register(String name) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, name), FabricParticleTypes.simple());
    }

    public static void registerParticles() {
        DomiXsCreatures.LOGGER.info("Registering ModParticles for " + DomiXsCreatures.MOD_ID);
    }
}
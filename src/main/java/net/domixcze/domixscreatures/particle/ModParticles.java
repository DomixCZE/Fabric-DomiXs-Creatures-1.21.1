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

    private static SimpleParticleType register(String name) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, name), FabricParticleTypes.simple());
    }

    public static void registerParticles() {
        DomiXsCreatures.LOGGER.info("Registering ModParticles for " + DomiXsCreatures.MOD_ID);
    }
}
package net.domixcze.domixscreatures.particle;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType SCREECH = FabricParticleTypes.simple();
    public static final DefaultParticleType INK = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "screech"), SCREECH);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "ink"), INK);
    }
}
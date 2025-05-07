package net.domixcze.domixscreatures.structures;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.StructureType;

public class ModStructures {

    public static StructureType<GroundStructures> GROUND_STRUCTURES;

    public static void registerStructureFeatures() {
        GROUND_STRUCTURES = Registry.register(Registries.STRUCTURE_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "ground_structures"), () -> GroundStructures.CODEC);
    }
}
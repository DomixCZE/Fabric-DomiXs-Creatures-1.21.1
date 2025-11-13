package net.domixcze.domixscreatures.world.gen;

public class ModWorldGeneration {
    public static void generateModWorldGen() {
        ModTreeGeneration.generateTrees();
        ModOreGeneration.generateOres();
        ModFlowerGeneration.generateFlowers();
    }
}
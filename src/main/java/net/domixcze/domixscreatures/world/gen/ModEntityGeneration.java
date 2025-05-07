package net.domixcze.domixscreatures.world.gen;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.domixcze.domixscreatures.entity.custom.WispEntity;
import net.domixcze.domixscreatures.entity.custom.WormEntity;
import net.domixcze.domixscreatures.util.ModTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;

public class ModEntityGeneration {
    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.FIRE_SALAMANDER_SPAWNS_IN), SpawnGroup.MONSTER,
                ModEntities.FIRE_SALAMANDER, 10, 1, 3);
        SpawnRestriction.register(ModEntities.FIRE_SALAMANDER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FireSalamanderEntity::canMobSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.WISP_SPAWNS_IN), SpawnGroup.AMBIENT,
                ModEntities.WISP, 10, 1, 2);
        SpawnRestriction.register(ModEntities.WISP, SpawnLocationTypes.UNRESTRICTED,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WispEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.IGUANA_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.IGUANA, 50, 1, 2);
        SpawnRestriction.register(ModEntities.IGUANA, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.TIGER_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.TIGER, 50, 1, 1);
        SpawnRestriction.register(ModEntities.TIGER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.CROCODILE_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.CROCODILE, 10, 1, 2);
        SpawnRestriction.register(ModEntities.CROCODILE, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.DEER_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.DEER, 15, 2, 4);
        SpawnRestriction.register(ModEntities.DEER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.MOOSE_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.MOOSE, 10, 2, 3);
        SpawnRestriction.register(ModEntities.MOOSE, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.HIPPO_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.HIPPO, 10, 2, 3);
        SpawnRestriction.register(ModEntities.HIPPO, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.MOLE_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.MOLE, 5, 0, 1);
        SpawnRestriction.register(ModEntities.MOLE, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.WORM_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.WORM, 20, 1, 3);
        SpawnRestriction.register(ModEntities.WORM, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WormEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.PORCUPINE_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.PORCUPINE, 5, 1, 2);
        SpawnRestriction.register(ModEntities.PORCUPINE, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.BOAR_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.BOAR, 5, 1, 2);
        SpawnRestriction.register(ModEntities.BOAR, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.BISON_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.BISON, 10, 3, 5);
        SpawnRestriction.register(ModEntities.BISON, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.WATER_STRIDER_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.WATER_STRIDER, 10, 0, 1);
        SpawnRestriction.register(ModEntities.WATER_STRIDER, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);




        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.WHALE_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.WHALE, 1, 0, 1);
        SpawnRestriction.register(ModEntities.WHALE, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.GOLDFISH_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.GOLDFISH, 10, 0, 1);
        SpawnRestriction.register(ModEntities.GOLDFISH, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.SHARK_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.SHARK, 1, 0, 1);
        SpawnRestriction.register(ModEntities.SHARK, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.EEL_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.EEL, 1, 0, 2);
        SpawnRestriction.register(ModEntities.EEL, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);
    }
}
package net.domixcze.domixscreatures.world.gen;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.AnglerfishEntity;
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

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.SUN_BEAR_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.SUN_BEAR, 10, 0, 1);
        SpawnRestriction.register(ModEntities.SUN_BEAR, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.CATERPILLAR_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.CATERPILLAR, 10, 0, 1);
        SpawnRestriction.register(ModEntities.CATERPILLAR, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.BUTTERFLY_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.BUTTERFLY, 10, 3, 5);
        SpawnRestriction.register(ModEntities.BUTTERFLY, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.GORILLA_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.GORILLA, 10, 3, 5);
        SpawnRestriction.register(ModEntities.GORILLA, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.CHEETAH_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.CHEETAH, 10, 0, 1);
        SpawnRestriction.register(ModEntities.CHEETAH, SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.HERMIT_CRAB_SPAWNS_IN), SpawnGroup.CREATURE,
                ModEntities.HERMIT_CRAB, 15, 1, 2);
        SpawnRestriction.register(ModEntities.HERMIT_CRAB, SpawnLocationTypes.ON_GROUND,
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

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.PIRANHA_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.PIRANHA, 10, 3, 8);
        SpawnRestriction.register(ModEntities.PIRANHA, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.PEACOCK_BASS_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.PEACOCK_BASS, 15, 0, 1);
        SpawnRestriction.register(ModEntities.PEACOCK_BASS, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.NEON_TETRA_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.NEON_TETRA, 10, 3, 8);
        SpawnRestriction.register(ModEntities.NEON_TETRA, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.BETTA_FISH_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.BETTA_FISH, 15, 1, 2);
        SpawnRestriction.register(ModEntities.BETTA_FISH, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.ANGLERFISH_SPAWNS_IN), SpawnGroup.UNDERGROUND_WATER_CREATURE,
                ModEntities.ANGLERFISH, 10, 0, 1);
        SpawnRestriction.register(ModEntities.ANGLERFISH, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnglerfishEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.tag(ModTags.Biomes.FRESHWATER_STINGRAY_SPAWNS_IN), SpawnGroup.WATER_CREATURE,
                ModEntities.FRESHWATER_STINGRAY, 5, 0, 1);
        SpawnRestriction.register(ModEntities.FRESHWATER_STINGRAY, SpawnLocationTypes.IN_WATER,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WaterCreatureEntity::canSpawn);
    }
}
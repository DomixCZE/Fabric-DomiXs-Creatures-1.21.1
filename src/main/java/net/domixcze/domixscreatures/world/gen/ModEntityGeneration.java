package net.domixcze.domixscreatures.world.gen;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.domixcze.domixscreatures.entity.custom.WispEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

public class ModEntityGeneration {
    public static void addSpawns() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.BASALT_DELTAS), SpawnGroup.MONSTER,
                ModEntities.FIRE_SALAMANDER, 10, 1, 3);
        SpawnRestriction.register(ModEntities.FIRE_SALAMANDER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FireSalamanderEntity::canMobSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.SOUL_SAND_VALLEY), SpawnGroup.AMBIENT,
                ModEntities.WISP, 10, 1, 2);
        SpawnRestriction.register(ModEntities.WISP, SpawnRestriction.Location.NO_RESTRICTIONS,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, WispEntity::canSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.DEEP_COLD_OCEAN), SpawnGroup.WATER_CREATURE,
                ModEntities.WHALE, 1, 0, 1);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.WARM_OCEAN), SpawnGroup.WATER_CREATURE,
                ModEntities.GOLDFISH, 10, 0, 1);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.JUNGLE), SpawnGroup.CREATURE,
                ModEntities.IGUANA, 50, 1, 2);
        SpawnRestriction.register(ModEntities.IGUANA, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.JUNGLE), SpawnGroup.CREATURE,
                ModEntities.TIGER, 50, 1, 1);
        SpawnRestriction.register(ModEntities.TIGER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.MANGROVE_SWAMP), SpawnGroup.CREATURE,
                ModEntities.CROCODILE, 15, 1, 2);
        SpawnRestriction.register(ModEntities.CROCODILE, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);

        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.TAIGA), SpawnGroup.CREATURE,
                ModEntities.DEER, 15, 2, 4);
        SpawnRestriction.register(ModEntities.DEER, SpawnRestriction.Location.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
    }
}
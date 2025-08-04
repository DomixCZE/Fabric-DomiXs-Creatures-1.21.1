package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModTags {
    public static class Blocks {

        public static final TagKey<Block> CROCODILE_EGG_HATCHABLE =
                createTag("crocodile_egg_hatchable");

        public static final TagKey<Block> WORM_SPAWNABLE_ON =
                createTag("worm_spawnable_on");

        public static final TagKey<Block> CAN_EAT =
                createTag("can_eat");

        public static final TagKey<Block> PALM_CAN_GROW_ON =
                createTag("palm_can_grow_on");

        public static final TagKey<Block> SPECTRAL_TREE_CAN_GROW_ON =
                createTag("spectral_tree_can_grow_on");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> CROCODILE_FOR_BREEDING =
                createTag("crocodile_for_breeding");

        public static final TagKey<Item> BEAVER_FOR_BREEDING =
                createTag("beaver_for_breeding");

        public static final TagKey<Item> BOAR_FOR_BREEDING =
                createTag("boar_for_breeding");

        public static final TagKey<Item> DEER_FOR_BREEDING =
                createTag("deer_for_breeding");

        public static final TagKey<Item> HIPPO_FOR_BREEDING =
                createTag("hippo_for_breeding");

        public static final TagKey<Item> IGUANA_FOR_BREEDING =
                createTag("iguana_for_breeding");

        public static final TagKey<Item> MOLE_FOR_BREEDING =
                createTag("mole_for_breeding");

        public static final TagKey<Item> MOOSE_FOR_BREEDING =
                createTag("moose_for_breeding");

        public static final TagKey<Item> PORCUPINE_FOR_BREEDING =
                createTag("porcupine_for_breeding");

        public static final TagKey<Item> SPECTRAL_BAT_FOR_BREEDING =
                createTag("spectral_bat_for_breeding");

        public static final TagKey<Item> TIGER_FOR_BREEDING =
                createTag("tiger_for_breeding");

        public static final TagKey<Item> BISON_FOR_BREEDING =
                createTag("bison_for_breeding");

        public static final TagKey<Item> SUN_BEAR_FOR_BREEDING =
                createTag("sun_bear_for_breeding");

        public static final TagKey<Item> BUTTERFLY_FOR_BREEDING =
                createTag("butterfly_for_breeding");

        public static final TagKey<Item> GORILLA_FOR_BREEDING =
                createTag("gorilla_for_breeding");

        public static final TagKey<Item> CHEETAH_FOR_BREEDING =
                createTag("cheetah_for_breeding");


        public static final TagKey<Item> HERMIT_CRAB_TEMPT =
                createTag("hermit_crab_tempt");


        public static final TagKey<Item> REFLECTIVE_ARMOR =
                createTag("reflective_armor");

        public static final TagKey<Item> PREVENTS_BLEEDING =
                createTag("prevents_bleeding");

        public static final TagKey<Item> ANTLERS =
                createTag("antlers");

        public static final TagKey<Item> TEETH =
                createTag("teeth");

        public static final TagKey<Item> ALL_ITEMS =
                createTag("all_items");

        public static final TagKey<Item> CONDUCTIVE_ITEMS =
                createTag("conductive_items");

        public static final TagKey<Item> DROPS_FROM_BOAR_DIGGING =
                createTag("drops_from_boar_digging");

        public static final TagKey<Item> RAW_MEAT =
                createTag("raw_meat");

        public static final TagKey<Item> CAN_BREAK_COCONUT =
                createTag("can_break_coconut");

        public static final TagKey<Item> MAGNETIC_ITEMS =
                createTag("magnetic_items");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_SAVANNA_CROCODILE = createTag("spawns_savanna_crocodile");
        public static final TagKey<Biome> SPAWNS_ABYSS_EEL = createTag("spawns_abyss_eel");
        public static final TagKey<Biome> SPAWNS_YELLOW_EEL = createTag("spawns_yellow_eel");
        public static final TagKey<Biome> BOAR_CAN_DIG_IN = createTag("boar_can_dig_in");
        public static final TagKey<Biome> SPAWNS_JUNGLE_BUTTERFLY = createTag("spawns_jungle_butterfly");
        public static final TagKey<Biome> SPAWNS_PLAINS_BUTTERFLY = createTag("spawns_plains_butterfly");
        public static final TagKey<Biome> SPAWNS_FOREST_BUTTERFLY = createTag("spawns_forest_butterfly");

        public static final TagKey<Biome> BOAR_SPAWNS_IN = createTag("boar_spawns_in");
        public static final TagKey<Biome> PORCUPINE_SPAWNS_IN = createTag("porcupine_spawns_in");
        public static final TagKey<Biome> WORM_SPAWNS_IN = createTag("worm_spawns_in");
        public static final TagKey<Biome> MOLE_SPAWNS_IN = createTag("mole_spawns_in");
        public static final TagKey<Biome> HIPPO_SPAWNS_IN = createTag("hippo_spawns_in");
        public static final TagKey<Biome> MOOSE_SPAWNS_IN = createTag("moose_spawns_in");
        public static final TagKey<Biome> DEER_SPAWNS_IN = createTag("deer_spawns_in");
        public static final TagKey<Biome> CROCODILE_SPAWNS_IN = createTag("crocodile_spawns_in");
        public static final TagKey<Biome> TIGER_SPAWNS_IN = createTag("tiger_spawns_in");
        public static final TagKey<Biome> IGUANA_SPAWNS_IN = createTag("iguana_spawns_in");
        public static final TagKey<Biome> WISP_SPAWNS_IN = createTag("wisp_spawns_in");
        public static final TagKey<Biome> FIRE_SALAMANDER_SPAWNS_IN = createTag("fire_salamander_spawns_in");
        public static final TagKey<Biome> BISON_SPAWNS_IN = createTag("bison_spawns_in");
        public static final TagKey<Biome> WATER_STRIDER_SPAWNS_IN = createTag("water_strider_spawns_in");
        public static final TagKey<Biome> SUN_BEAR_SPAWNS_IN = createTag("sun_bear_spawns_in");
        public static final TagKey<Biome> BUTTERFLY_SPAWNS_IN = createTag("butterfly_spawns_in");
        public static final TagKey<Biome> CATERPILLAR_SPAWNS_IN = createTag("caterpillar_spawns_in");
        public static final TagKey<Biome> GORILLA_SPAWNS_IN = createTag("gorilla_spawns_in");
        public static final TagKey<Biome> CHEETAH_SPAWNS_IN = createTag("cheetah_spawns_in");
        public static final TagKey<Biome> HERMIT_CRAB_SPAWNS_IN = createTag("hermit_crab_spawns_in");

        public static final TagKey<Biome> WHALE_SPAWNS_IN = createTag("whale_spawns_in");
        public static final TagKey<Biome> GOLDFISH_SPAWNS_IN = createTag("goldfish_spawns_in");
        public static final TagKey<Biome> SHARK_SPAWNS_IN = createTag("shark_spawns_in");
        public static final TagKey<Biome> EEL_SPAWNS_IN = createTag("eel_spawns_in");
        public static final TagKey<Biome> PIRANHA_SPAWNS_IN = createTag("piranha_spawns_in");
        public static final TagKey<Biome> NEON_TETRA_SPAWNS_IN = createTag("neon_tetra_spawns_in");
        public static final TagKey<Biome> PEACOCK_BASS_SPAWNS_IN = createTag("peacock_bass_spawns_in");
        public static final TagKey<Biome> BETTA_FISH_SPAWNS_IN = createTag("betta_fish_spawns_in");
        public static final TagKey<Biome> ARAPAIMA_SPAWNS_IN = createTag("arapaima_spawns_in");
        public static final TagKey<Biome> FRESHWATER_STINGRAY_SPAWNS_IN = createTag("freshwater_stingray_spawns_in");
        public static final TagKey<Biome> ANGLERFISH_SPAWNS_IN = createTag("anglerfish_spawns_in");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.of(RegistryKeys.BIOME, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> ERASABLE =
                createTag("erasable");

        public static final TagKey<EntityType<?>> DEER_FLEE_FROM =
                createTag("deer_flee_from");

        public static final TagKey<EntityType<?>> NOT_SCARY_FOR_FRESHWATER_STINGRAY =
                createTag("not_scary_for_freshwater_stingray");

        public static final TagKey<EntityType<?>> REFLECTABLE_PROJECTILES =
                createTag("reflectable_projectiles");

        public static final TagKey<EntityType<?>> CAPTURABLE =
                createTag("capturable");

        private static TagKey<EntityType<?>> createTag(String name) {
            return TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }
}
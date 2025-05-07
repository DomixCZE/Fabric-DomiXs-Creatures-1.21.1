package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.block.Block;
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



        public static final TagKey<Item> PREVENTS_BLEEDING =
                createTag("prevents_bleeding");

        public static final TagKey<Item> ANTLERS =
                createTag("antlers");

        public static final TagKey<Item> ALL_ITEMS =
                createTag("all_items");

        public static final TagKey<Item> CONDUCTIVE_ITEMS =
                createTag("conductive_items");

        public static final TagKey<Item> DROPS_FROM_BOAR_DIGGING =
                createTag("drops_from_boar_digging");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_SAVANNA_CROCODILE = createTag("spawns_savanna_crocodile");
        public static final TagKey<Biome> SPAWNS_ABYSS_EEL = createTag("spawns_abyss_eel");
        public static final TagKey<Biome> SPAWNS_YELLOW_EEL = createTag("spawns_yellow_eel");
        public static final TagKey<Biome> BOAR_CAN_DIG_IN = createTag("boar_can_dig_in");

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

        public static final TagKey<Biome> WHALE_SPAWNS_IN = createTag("whale_spawns_in");
        public static final TagKey<Biome> GOLDFISH_SPAWNS_IN = createTag("goldfish_spawns_in");
        public static final TagKey<Biome> SHARK_SPAWNS_IN = createTag("shark_spawns_in");
        public static final TagKey<Biome> EEL_SPAWNS_IN = createTag("eel_spawns_in");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.of(RegistryKeys.BIOME, Identifier.of(DomiXsCreatures.MOD_ID, name));
        }
    }
}
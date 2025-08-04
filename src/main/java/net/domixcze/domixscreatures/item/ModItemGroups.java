package net.domixcze.domixscreatures.item;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup MOD_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(DomiXsCreatures.MOD_ID, "mod_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.mod_group"))
                    .icon(() -> new ItemStack(ModItems.TAB_ICON)).entries((displayContext, entries) -> {
                        entries.add(ModItems.ERASER);
                        entries.add(ModItems.GUIDE_BOOK);
                        entries.add(ModItems.FIRE_SALAMANDER_SPAWN_EGG);
                        entries.add(ModItems.SPECTRAL_BAT_SPAWN_EGG);
                        entries.add(ModItems.WHALE_SPAWN_EGG);
                        entries.add(ModItems.GOLDFISH_SPAWN_EGG);
                        entries.add(ModItems.WISP_SPAWN_EGG);
                        entries.add(ModItems.BEAVER_SPAWN_EGG);
                        entries.add(ModItems.IGUANA_SPAWN_EGG);
                        entries.add(ModItems.TIGER_SPAWN_EGG);
                        entries.add(ModItems.DEER_SPAWN_EGG);
                        entries.add(ModItems.MOOSE_SPAWN_EGG);
                        entries.add(ModItems.SHARK_SPAWN_EGG);
                        entries.add(ModItems.EEL_SPAWN_EGG);
                        entries.add(ModItems.HIPPO_SPAWN_EGG);
                        entries.add(ModItems.SHAMAN_SPAWN_EGG);
                        entries.add(ModItems.VINE_SPAWN_EGG);
                        entries.add(ModItems.MUD_GOLEM_SPAWN_EGG);
                        entries.add(ModItems.MOLE_SPAWN_EGG);
                        entries.add(ModItems.WORM_SPAWN_EGG);
                        entries.add(ModItems.PORCUPINE_SPAWN_EGG);
                        entries.add(ModItems.WATER_STRIDER_SPAWN_EGG);
                        entries.add(ModItems.BOAR_SPAWN_EGG);
                        entries.add(ModItems.BISON_SPAWN_EGG);
                        entries.add(ModItems.SUN_BEAR_SPAWN_EGG);
                        entries.add(ModItems.CATERPILLAR_SPAWN_EGG);
                        entries.add(ModItems.BUTTERFLY_SPAWN_EGG);
                        entries.add(ModItems.GORILLA_SPAWN_EGG);
                        entries.add(ModItems.PIRANHA_SPAWN_EGG);
                        entries.add(ModItems.NEON_TETRA_SPAWN_EGG);
                        entries.add(ModItems.PEACOCK_BASS_SPAWN_EGG);
                        entries.add(ModItems.BETTA_FISH_SPAWN_EGG);
                        entries.add(ModItems.ARAPAIMA_SPAWN_EGG);
                        entries.add(ModItems.ANGLERFISH_SPAWN_EGG);
                        entries.add(ModItems.FRESHWATER_STINGRAY_SPAWN_EGG);
                        entries.add(ModItems.CHEETAH_SPAWN_EGG);
                        entries.add(ModItems.HERMIT_CRAB_SPAWN_EGG);
                        entries.add(ModItems.CROCODILE_SPAWN_EGG);

                        entries.add(ModBlocks.CROCODILE_EGG);
                        entries.add(ModItems.PIRANHA_BUCKET);
                        entries.add(ModItems.NEON_TETRA_BUCKET);
                        entries.add(ModItems.PEACOCK_BASS_BUCKET);
                        entries.add(ModItems.BETTA_FISH_BUCKET);
                        entries.add(ModItems.ANGLERFISH_BUCKET);
                        entries.add(ModItems.FRESHWATER_STINGRAY_BUCKET);
                        entries.add(ModItems.GOLDFISH_BUCKET);
                        entries.add(ModItems.GOLDFISH);
                        entries.add(ModItems.WORM);
                        entries.add(ModItems.RAW_DEER_VENISON);
                        entries.add(ModItems.COOKED_DEER_VENISON);
                        entries.add(ModItems.RAW_BISON_MEAT);
                        entries.add(ModItems.COOKED_BISON_MEAT);
                        entries.add(ModItems.TRUFFLE);
                        entries.add(ModItems.HONEYED_APPLE);
                        entries.add(ModItems.BANANA);
                        entries.add(ModItems.SPECTRAL_FRUIT);
                        entries.add(ModBlocks.COCONUT_BLOCK);
                        entries.add(ModItems.COCONUT_SLICE);
                        entries.add(ModItems.COCONUT_SHELL);
                        entries.add(ModItems.SPECTRAL_BAT_WING);
                        entries.add(ModItems.SPECTRAL_BAT_EAR);
                        entries.add(ModItems.FIRE_SALAMANDER_SCALES);
                        entries.add(ModItems.CROCODILE_SCALE);
                        entries.add(ModItems.CROCODILE_SCALE_ALBINO);
                        entries.add(ModItems.ARAPAIMA_SCALE_BLACK);
                        entries.add(ModItems.ARAPAIMA_SCALE_GREEN);
                        entries.add(ModItems.CROCODILE_TOOTH);
                        entries.add(ModItems.SHARK_TOOTH);
                        entries.add(ModItems.SMALL_ANTLER);
                        entries.add(ModItems.MEDIUM_ANTLER);
                        entries.add(ModItems.LARGE_ANTLER);
                        entries.add(ModItems.QUILL);
                        entries.add(ModItems.SKULL);
                        entries.add(ModItems.SKULL_WAND);
                        entries.add(ModItems.BUG_NET);
                        entries.add(ModItems.NIGHTMARE_AMULET);
                        entries.add(ModItems.CONCH_SHELL);
                        entries.add(ModItems.WATER_LILY);
                        entries.add(ModItems.BARK);
                        entries.add(ModItems.SAWDUST);
                        entries.add(ModItems.PEARL);

                        entries.add(ModItems.WARDEN_TENDRIL);
                        entries.add(ModItems.SONIC_BLOCKERS);
                        entries.add(ModItems.ADVENTURER_HAT);
                        entries.add(ModItems.SHAMAN_MASK);
                        entries.add(ModItems.CROCODILE_CHESTPLATE);
                        entries.add(ModItems.CROCODILE_CHESTPLATE_ALBINO);
                        entries.add(ModItems.ARAPAIMA_HELMET_BLACK);
                        entries.add(ModItems.ARAPAIMA_CHESTPLATE_BLACK);
                        entries.add(ModItems.ARAPAIMA_LEGGINGS_BLACK);
                        entries.add(ModItems.ARAPAIMA_BOOTS_BLACK);
                        entries.add(ModItems.ARAPAIMA_HELMET_GREEN);
                        entries.add(ModItems.ARAPAIMA_CHESTPLATE_GREEN);
                        entries.add(ModItems.ARAPAIMA_LEGGINGS_GREEN);
                        entries.add(ModItems.ARAPAIMA_BOOTS_GREEN);
                        entries.add(ModItems.SALAMANDER_HELMET);
                        entries.add(ModItems.SALAMANDER_CHESTPLATE);
                        entries.add(ModItems.SALAMANDER_LEGGINGS);
                        entries.add(ModItems.SALAMANDER_BOOTS);
                        entries.add(ModItems.SALAMANDER_UPGRADE_SMITHING_TEMPLATE);

                        entries.add(ModBlocks.MAGNETITE_ORE);
                        entries.add(ModBlocks.DEEPSLATE_MAGNETITE_ORE);
                        entries.add(ModItems.RAW_MAGNETITE);
                        entries.add(ModItems.MAGNETITE_INGOT);
                        entries.add(ModBlocks.BLOCK_OF_MAGNETITE);
                        entries.add(ModBlocks.NEGATIVE_MAGNET_BLOCK);
                        entries.add(ModBlocks.POSITIVE_MAGNET_BLOCK);
                        entries.add(ModItems.MAGNET);

                        entries.add(ModItems.MUD_BLOSSOM_SEED);

                        entries.add(ModBlocks.BARNACLE_BLOCK);
                        entries.add(ModBlocks.CRACKED_GLASS_BLOCK);
                        entries.add(ModBlocks.PILE_OF_STICKS_BLOCK);
                        entries.add(ModBlocks.SAWDUST_BLOCK);
                        entries.add(ModBlocks.MOLEHILL_BLOCK);
                        entries.add(ModBlocks.PEARL_BLOCK);

                        entries.add(ModBlocks.SPECTRAL_LOG);
                        entries.add(ModBlocks.SPECTRAL_WOOD);
                        entries.add(ModBlocks.STRIPPED_SPECTRAL_LOG);
                        entries.add(ModBlocks.STRIPPED_SPECTRAL_WOOD);
                        entries.add(ModBlocks.SPECTRAL_PLANKS);
                        entries.add(ModBlocks.SPECTRAL_STAIRS);
                        entries.add(ModBlocks.SPECTRAL_SLAB);
                        entries.add(ModBlocks.SPECTRAL_FENCE);
                        entries.add(ModBlocks.SPECTRAL_FENCE_GATE);
                        entries.add(ModBlocks.SPECTRAL_PRESSURE_PLATE);
                        entries.add(ModBlocks.SPECTRAL_BUTTON);
                        entries.add(ModBlocks.SPECTRAL_DOOR);
                        entries.add(ModBlocks.SPECTRAL_TRAPDOOR);
                        entries.add(ModBlocks.SPECTRAL_LEAVES);
                        entries.add(ModBlocks.SPECTRAL_SAPLING);

                        entries.add(ModBlocks.PALM_LOG);
                        entries.add(ModBlocks.PALM_WOOD);
                        entries.add(ModBlocks.STRIPPED_PALM_LOG);
                        entries.add(ModBlocks.STRIPPED_PALM_WOOD);
                        entries.add(ModBlocks.PALM_PLANKS);
                        entries.add(ModBlocks.PALM_STAIRS);
                        entries.add(ModBlocks.PALM_SLAB);
                        entries.add(ModBlocks.PALM_FENCE);
                        entries.add(ModBlocks.PALM_FENCE_GATE);
                        entries.add(ModBlocks.PALM_PRESSURE_PLATE);
                        entries.add(ModBlocks.PALM_BUTTON);
                        entries.add(ModBlocks.PALM_DOOR);
                        entries.add(ModBlocks.PALM_TRAPDOOR);
                        entries.add(ModBlocks.PALM_LEAVES);
                        entries.add(ModBlocks.PALM_SAPLING);

                    }).build());

    public static void registerItemGroups() {
        DomiXsCreatures.LOGGER.info("Registering Item Groups for " + DomiXsCreatures.MOD_ID);
    }
}
package net.domixcze.domixscreatures.item;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.item.custom.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item FIRE_SALAMANDER_SPAWN_EGG = registerItem("fire_salamander_spawn_egg", new SpawnEggItem(ModEntities.FIRE_SALAMANDER, 0x3c3947, 0xe66410,
            new FabricItemSettings()));
    public static final Item SPECTRAL_BAT_SPAWN_EGG = registerItem("spectral_bat_spawn_egg", new SpawnEggItem(ModEntities.SPECTRAL_BAT, 0x383e59, 0x7e74fe,
            new FabricItemSettings()));
    public static final Item WHALE_SPAWN_EGG = registerItem("whale_spawn_egg", new SpawnEggItem(ModEntities.WHALE, 0x1c1b1b, 0xaba79e,
            new FabricItemSettings()));
    public static final Item GOLDFISH_SPAWN_EGG = registerItem("goldfish_spawn_egg", new SpawnEggItem(ModEntities.GOLDFISH, 0xffaa21, 0xff771c,
            new FabricItemSettings()));
    public static final Item WISP_SPAWN_EGG = registerItem("wisp_spawn_egg", new SpawnEggItem(ModEntities.WISP, 0x73bed3, 0xa4dddb,
            new FabricItemSettings()));
    public static final Item CROCODILE_SPAWN_EGG = registerItem("crocodile_spawn_egg", new SpawnEggItem(ModEntities.CROCODILE, 0x373e2b, 0x8faf97,
            new FabricItemSettings()));
    public static final Item BEAVER_SPAWN_EGG = registerItem("beaver_spawn_egg", new SpawnEggItem(ModEntities.BEAVER, 0x52362e, 0x704c3d,
            new FabricItemSettings()));
    public static final Item IGUANA_SPAWN_EGG = registerItem("iguana_spawn_egg", new SpawnEggItem(ModEntities.IGUANA, 0x4b8238, 0xe57110,
            new FabricItemSettings()));
    public static final Item TIGER_SPAWN_EGG = registerItem("tiger_spawn_egg", new SpawnEggItem(ModEntities.TIGER, 0xda7915, 0x161617,
            new FabricItemSettings()));
    public static final Item DEER_SPAWN_EGG = registerItem("deer_spawn_egg", new SpawnEggItem(ModEntities.DEER, 0x61452e, 0xcba379,
            new FabricItemSettings()));
    public static final Item MOOSE_SPAWN_EGG = registerItem("moose_spawn_egg", new SpawnEggItem(ModEntities.MOOSE, 0x3c2f29, 0x61493a,
            new FabricItemSettings()));
    public static final Item SHARK_SPAWN_EGG = registerItem("shark_spawn_egg", new SpawnEggItem(ModEntities.SHARK, 0x2a3d52, 0x4b7586,
            new FabricItemSettings()));
    public static final Item EEL_SPAWN_EGG = registerItem("eel_spawn_egg", new SpawnEggItem(ModEntities.EEL, 0x3c671a, 0x628923,
            new FabricItemSettings()));
    public static final Item HIPPO_SPAWN_EGG = registerItem("hippo_spawn_egg", new SpawnEggItem(ModEntities.HIPPO, 0x6d444c, 0xd38b8c,
            new FabricItemSettings()));
    public static final Item SHAMAN_SPAWN_EGG = registerItem("shaman_spawn_egg", new SpawnEggItem(ModEntities.SHAMAN, 0x464420, 0xbe886c,
            new FabricItemSettings()));
    public static final Item VINE_SPAWN_EGG = registerItem("vine_spawn_egg", new SpawnEggItem(ModEntities.VINE, 0x445b23, 0x5d852b,
            new FabricItemSettings()));
    public static final Item MUD_GOLEM_SPAWN_EGG = registerItem("mud_golem_spawn_egg", new SpawnEggItem(ModEntities.MUD_GOLEM, 0x7e5d48, 0xab8661,
            new FabricItemSettings()));
    public static final Item MOLE_SPAWN_EGG = registerItem("mole_spawn_egg", new SpawnEggItem(ModEntities.MOLE, 0x262626, 0x3b3b3b,
            new FabricItemSettings()));
    public static final Item WORM_SPAWN_EGG = registerItem("worm_spawn_egg", new SpawnEggItem(ModEntities.WORM, 0xbb7697, 0xd1a8bc,
            new FabricItemSettings()));

    public static final Item GOLDFISH_BUCKET = registerItem("goldfish_bucket", new EntityBucketItem(ModEntities.GOLDFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new FabricItemSettings().maxCount(1)));

    public static final Item GOLDFISH = registerItem("goldfish", new Item(new FabricItemSettings().food(ModFoodComponents.GOLDFISH)));
    public static final Item WORM = registerItem("worm", new Item(new FabricItemSettings().food(ModFoodComponents.WORM)));
    public static final Item SPECTRAL_FRUIT = registerItem("spectral_fruit", new Item(new FabricItemSettings().food(ModFoodComponents.SPECTRAL_FRUIT)));

    public static final Item SKULL = registerItem("skull", new Item(new FabricItemSettings()));
    public static final Item FIRE_SALAMANDER_SCALES = registerItem("fire_salamander_scales", new Item(new FabricItemSettings()));
    public static final Item SPECTRAL_BAT_WING = registerItem("spectral_bat_wing", new Item(new FabricItemSettings()));
    public static final Item SPECTRAL_BAT_EAR = registerItem("spectral_bat_ear", new Item(new FabricItemSettings()));
    public static final Item CROCODILE_SCALE = registerItem("crocodile_scale", new Item(new FabricItemSettings()));
    public static final Item CROCODILE_SCALE_ALBINO = registerItem("crocodile_scale_albino", new Item(new FabricItemSettings()));
    public static final Item CROCODILE_TOOTH = registerItem("crocodile_tooth", new Item(new FabricItemSettings()));
    public static final Item WATER_LILY = registerItem("water_lily", new Item(new FabricItemSettings()));
    public static final Item BARK = registerItem("bark", new Item(new FabricItemSettings()));
    public static final Item SAWDUST = registerItem("sawdust", new Item(new FabricItemSettings()));
    public static final Item WARDEN_TENDRIL = registerItem("warden_tendril", new Item(new FabricItemSettings()));
    public static final Item SHARK_TOOTH = registerItem("shark_tooth", new Item(new FabricItemSettings()));

    public static final Item SMALL_ANTLER = registerItem("small_antler", new SmallAntlerItem(new FabricItemSettings()));
    public static final Item MEDIUM_ANTLER = registerItem("medium_antler", new MediumAntlerItem(new FabricItemSettings()));
    public static final Item LARGE_ANTLER = registerItem("large_antler", new LargeAntlerItem(new FabricItemSettings()));

    public static final Item ADVENTURER_HAT = registerItem("adventurer_hat", new AdventurerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item SONIC_BLOCKERS = registerItem("sonic_blockers", new SonicBlockersItem(ModArmorMaterials.UTILITY, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item SHAMAN_MASK = registerItem("shaman_mask", new ShamanMaskItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new FabricItemSettings()));

    public static final Item SMALL_ANTLER_HAT = registerItem("small_antler_hat", new SmallAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item MEDIUM_ANTLER_HAT = registerItem("medium_antler_hat", new MediumAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item LARGE_ANTLER_HAT = registerItem("large_antler_hat", new LargeAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new FabricItemSettings()));

    public static final Item CROCODILE_CHESTPLATE = registerItem("crocodile_chestplate", new CrocodileChestplateItem(ModArmorMaterials.CROCODILE, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item CROCODILE_CHESTPLATE_ALBINO = registerItem("crocodile_chestplate_albino", new CrocodileChestplateAlbinoItem(ModArmorMaterials.CROCODILE_ALBINO, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));

    public static final Item SKULL_WAND = registerItem("skull_wand", new Item(new FabricItemSettings().maxCount(1)));
    public static final Item NIGHTMARE_AMULET = registerItem("nightmare_amulet", new NightmareAmuletItem(new FabricItemSettings().maxCount(1)));

    public static final Item DEAFEN_ICON = registerItem("deafen_icon", new Item(new FabricItemSettings()));
    public static final Item TAB_ICON = registerItem("tab_icon", new Item(new FabricItemSettings()));

    public static final Item MUD_BLOSSOM_SEED = registerItem("mud_blossom_seed", new AliasedBlockItem(ModBlocks.MUD_BLOSSOM, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(DomiXsCreatures.MOD_ID, name), item);
    }

    public static void registerModItems() {
        DomiXsCreatures.LOGGER.info("Registering Items for " + DomiXsCreatures.MOD_ID);
    }
}
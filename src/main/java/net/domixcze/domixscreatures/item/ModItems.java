package net.domixcze.domixscreatures.item;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.item.custom.*;
import net.domixcze.domixscreatures.item.guide.GuideBookItem;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item FIRE_SALAMANDER_SPAWN_EGG = registerItem("fire_salamander_spawn_egg", new SpawnEggItem(ModEntities.FIRE_SALAMANDER, 0x3c3947, 0xe66410,
            new Item.Settings()));
    public static final Item SPECTRAL_BAT_SPAWN_EGG = registerItem("spectral_bat_spawn_egg", new SpawnEggItem(ModEntities.SPECTRAL_BAT, 0x383e59, 0x7e74fe,
            new Item.Settings()));
    public static final Item WHALE_SPAWN_EGG = registerItem("whale_spawn_egg", new SpawnEggItem(ModEntities.WHALE, 0x1c1b1b, 0xaba79e,
            new Item.Settings()));
    public static final Item GOLDFISH_SPAWN_EGG = registerItem("goldfish_spawn_egg", new SpawnEggItem(ModEntities.GOLDFISH, 0xffaa21, 0xff771c,
            new Item.Settings()));
    public static final Item WISP_SPAWN_EGG = registerItem("wisp_spawn_egg", new SpawnEggItem(ModEntities.WISP, 0x73bed3, 0xa4dddb,
            new Item.Settings()));
    public static final Item CROCODILE_SPAWN_EGG = registerItem("crocodile_spawn_egg", new SpawnEggItem(ModEntities.CROCODILE, 0x373e2b, 0x8faf97,
            new Item.Settings()));
    public static final Item BEAVER_SPAWN_EGG = registerItem("beaver_spawn_egg", new SpawnEggItem(ModEntities.BEAVER, 0x52362e, 0x704c3d,
            new Item.Settings()));
    public static final Item IGUANA_SPAWN_EGG = registerItem("iguana_spawn_egg", new SpawnEggItem(ModEntities.IGUANA, 0x4b8238, 0xe57110,
            new Item.Settings()));
    public static final Item TIGER_SPAWN_EGG = registerItem("tiger_spawn_egg", new SpawnEggItem(ModEntities.TIGER, 0xda7915, 0x161617,
            new Item.Settings()));
    public static final Item DEER_SPAWN_EGG = registerItem("deer_spawn_egg", new SpawnEggItem(ModEntities.DEER, 0x61452e, 0xcba379,
            new Item.Settings()));
    public static final Item MOOSE_SPAWN_EGG = registerItem("moose_spawn_egg", new SpawnEggItem(ModEntities.MOOSE, 0x3c2f29, 0x61493a,
            new Item.Settings()));
    public static final Item SHARK_SPAWN_EGG = registerItem("shark_spawn_egg", new SpawnEggItem(ModEntities.SHARK, 0x2a3d52, 0x4b7586,
            new Item.Settings()));
    public static final Item EEL_SPAWN_EGG = registerItem("eel_spawn_egg", new SpawnEggItem(ModEntities.EEL, 0x3c671a, 0x628923,
            new Item.Settings()));
    public static final Item HIPPO_SPAWN_EGG = registerItem("hippo_spawn_egg", new SpawnEggItem(ModEntities.HIPPO, 0x6d444c, 0xd38b8c,
            new Item.Settings()));
    public static final Item SHAMAN_SPAWN_EGG = registerItem("shaman_spawn_egg", new SpawnEggItem(ModEntities.SHAMAN, 0x464420, 0xbe886c,
            new Item.Settings()));
    public static final Item VINE_SPAWN_EGG = registerItem("vine_spawn_egg", new SpawnEggItem(ModEntities.VINE, 0x445b23, 0x5d852b,
            new Item.Settings()));
    public static final Item MUD_GOLEM_SPAWN_EGG = registerItem("mud_golem_spawn_egg", new SpawnEggItem(ModEntities.MUD_GOLEM, 0x7e5d48, 0xab8661,
            new Item.Settings()));
    public static final Item MOLE_SPAWN_EGG = registerItem("mole_spawn_egg", new SpawnEggItem(ModEntities.MOLE, 0x262626, 0x3b3b3b,
            new Item.Settings()));
    public static final Item WORM_SPAWN_EGG = registerItem("worm_spawn_egg", new SpawnEggItem(ModEntities.WORM, 0xbb7697, 0xd1a8bc,
            new Item.Settings()));
    public static final Item PORCUPINE_SPAWN_EGG = registerItem("porcupine_spawn_egg", new SpawnEggItem(ModEntities.PORCUPINE, 0x342b27, 0xb5a8a7,
            new Item.Settings()));
    public static final Item WATER_STRIDER_SPAWN_EGG = registerItem("water_strider_spawn_egg", new SpawnEggItem(ModEntities.WATER_STRIDER, 0x433732, 0x4e4843,
            new Item.Settings()));
    public static final Item BOAR_SPAWN_EGG = registerItem("boar_spawn_egg", new SpawnEggItem(ModEntities.BOAR, 0x221d1b, 0x352d2a,
            new Item.Settings()));
    public static final Item BISON_SPAWN_EGG = registerItem("bison_spawn_egg", new SpawnEggItem(ModEntities.BISON, 0x513c29, 0x49362d,
            new Item.Settings()));
    public static final Item SUN_BEAR_SPAWN_EGG = registerItem("sun_bear_spawn_egg", new SpawnEggItem(ModEntities.SUN_BEAR, 0x090812, 0xc28854,
            new Item.Settings()));
    public static final Item CATERPILLAR_SPAWN_EGG = registerItem("caterpillar_spawn_egg", new SpawnEggItem(ModEntities.CATERPILLAR, 0x628d04, 0xce9100,
            new Item.Settings()));
    public static final Item BUTTERFLY_SPAWN_EGG = registerItem("butterfly_spawn_egg", new SpawnEggItem(ModEntities.BUTTERFLY, 0x391b24, 0xc11102,
            new Item.Settings()));
    public static final Item GORILLA_SPAWN_EGG = registerItem("gorilla_spawn_egg", new SpawnEggItem(ModEntities.GORILLA, 0x2b2a27, 0x3f3b3b,
            new Item.Settings()));
    public static final Item PIRANHA_SPAWN_EGG = registerItem("piranha_spawn_egg", new SpawnEggItem(ModEntities.PIRANHA, 0x577b82, 0xd5690f,
            new Item.Settings()));
    public static final Item NEON_TETRA_SPAWN_EGG = registerItem("neon_tetra_spawn_egg", new SpawnEggItem(ModEntities.NEON_TETRA, 0x03c2e2, 0xcf3232,
            new Item.Settings()));
    public static final Item PEACOCK_BASS_SPAWN_EGG = registerItem("peacock_bass_spawn_egg", new SpawnEggItem(ModEntities.PEACOCK_BASS, 0x737e41, 0x0e1006,
            new Item.Settings()));
    public static final Item BETTA_FISH_SPAWN_EGG = registerItem("betta_fish_spawn_egg", new SpawnEggItem(ModEntities.BETTA_FISH, 0xcc7d93, 0xc8c6d3,
            new Item.Settings()));
    public static final Item ARAPAIMA_SPAWN_EGG = registerItem("arapaima_spawn_egg", new SpawnEggItem(ModEntities.ARAPAIMA, 0x4c5e20, 0x9c3a40,
            new Item.Settings()));
    public static final Item ANGLERFISH_SPAWN_EGG = registerItem("anglerfish_spawn_egg", new SpawnEggItem(ModEntities.ANGLERFISH, 0x232d3d, 0xa1ffd8,
            new Item.Settings()));
    public static final Item FRESHWATER_STINGRAY_SPAWN_EGG = registerItem("freshwater_stingray_spawn_egg", new SpawnEggItem(ModEntities.FRESHWATER_STINGRAY, 0xa0864b, 0x4b432e,
            new Item.Settings()));
    public static final Item CHEETAH_SPAWN_EGG = registerItem("cheetah_spawn_egg", new SpawnEggItem(ModEntities.CHEETAH, 0xf0b058, 0x78481f,
            new Item.Settings()));
    public static final Item HERMIT_CRAB_SPAWN_EGG = registerItem("hermit_crab_spawn_egg", new SpawnEggItem(ModEntities.HERMIT_CRAB, 0xb25c32, 0xb97aa8,
            new Item.Settings()));
    public static final Item RACCOON_SPAWN_EGG = registerItem("raccoon_spawn_egg", new SpawnEggItem(ModEntities.RACCOON, 0x5a5a5a, 0x1e1e1e,
            new Item.Settings()));
    public static final Item CAPYBARA_SPAWN_EGG = registerItem("capybara_spawn_egg", new SpawnEggItem(ModEntities.CAPYBARA, 0xa96b38, 0x854b28,
            new Item.Settings()));
    public static final Item HYENA_SPAWN_EGG = registerItem("hyena_spawn_egg", new SpawnEggItem(ModEntities.HYENA, 0x7a6045, 0x4a3a30,
            new Item.Settings()));
    public static final Item ANCIENT_SKELETON_SPAWN_EGG = registerItem("ancient_skeleton_spawn_egg", new SpawnEggItem(ModEntities.ANCIENT_SKELETON, 0x5d423a, 0x9a6c61,
            new Item.Settings()));
    public static final Item UNICORN_SPAWN_EGG = registerItem("unicorn_spawn_egg", new SpawnEggItem(ModEntities.UNICORN, 0xe8f3f5, 0xce96e4,
            new Item.Settings()));
    public static final Item CATFISH_SPAWN_EGG = registerItem("catfish_spawn_egg", new SpawnEggItem(ModEntities.CATFISH, 0x827537, 0x8a3b0e,
            new Item.Settings()));

    public static final Item GOLDFISH_BUCKET = registerItem("goldfish_bucket", new EntityBucketItem(ModEntities.GOLDFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item PIRANHA_BUCKET = registerItem("piranha_bucket", new EntityBucketItem(ModEntities.PIRANHA, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item NEON_TETRA_BUCKET = registerItem("neon_tetra_bucket", new EntityBucketItem(ModEntities.NEON_TETRA, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item PEACOCK_BASS_BUCKET = registerItem("peacock_bass_bucket", new EntityBucketItem(ModEntities.PEACOCK_BASS, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item BETTA_FISH_BUCKET = registerItem("betta_fish_bucket", new EntityBucketItem(ModEntities.BETTA_FISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item ANGLERFISH_BUCKET = registerItem("anglerfish_bucket", new EntityBucketItem(ModEntities.ANGLERFISH, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));
    public static final Item FRESHWATER_STINGRAY_BUCKET = registerItem("freshwater_stingray_bucket", new EntityBucketItem(ModEntities.FRESHWATER_STINGRAY, Fluids.WATER, SoundEvents.ITEM_BUCKET_EMPTY_FISH,
            new Item.Settings().maxCount(1)));

    public static final Item GOLDFISH = registerItem("goldfish", new Item(new Item.Settings().food(ModFoodComponents.GOLDFISH)));
    public static final Item WORM = registerItem("worm", new Item(new Item.Settings().food(ModFoodComponents.WORM)));
    public static final Item SPECTRAL_FRUIT = registerItem("spectral_fruit", new Item(new Item.Settings().food(ModFoodComponents.SPECTRAL_FRUIT)));
    public static final Item HONEYED_APPLE = registerItem("honeyed_apple", new Item(new Item.Settings().food(ModFoodComponents.HONEYED_APPLE)));
    public static final Item BANANA = registerItem("banana", new Item(new Item.Settings().food(ModFoodComponents.BANANA)));
    public static final Item RAW_BISON_MEAT = registerItem("raw_bison_meat", new Item(new Item.Settings().food(ModFoodComponents.BISON_MEAT)));
    public static final Item COOKED_BISON_MEAT = registerItem("cooked_bison_meat", new Item(new Item.Settings().food(ModFoodComponents.COOKED_BISON_MEAT)));
    public static final Item RAW_DEER_VENISON = registerItem("raw_deer_venison", new Item(new Item.Settings().food(ModFoodComponents.DEER_VENISON)));
    public static final Item COOKED_DEER_VENISON = registerItem("cooked_deer_venison", new Item(new Item.Settings().food(ModFoodComponents.COOKED_DEER_VENISON)));
    public static final Item RAW_EEL_MEAT = registerItem("raw_eel_meat", new Item(new Item.Settings().food(ModFoodComponents.EEL_MEAT)));
    public static final Item COOKED_EEL_MEAT = registerItem("cooked_eel_meat", new Item(new Item.Settings().food(ModFoodComponents.COOKED_EEL_MEAT)));

    public static final Item COCONUT_SLICE = registerItem("coconut_slice", new CoconutSliceItem(new Item.Settings().food(ModFoodComponents.COCONUT_SLICE)));
    public static final Item MARSHMALLOW = registerItem("marshmallow", new Item(new Item.Settings().maxCount(16)));
    public static final Item MARSHMALLOW_STICK = registerItem("marshmallow_stick", new MarshmallowStickItem(new Item.Settings().maxCount(1)));
    public static final Item COOKED_MARSHMALLOW_STICK = registerItem("cooked_marshmallow_stick", new CookedMarshmallowStickItem(new Item.Settings().maxCount(1).food(ModFoodComponents.COOKED_MARSHMALLOW)));
    public static final Item BURNT_MARSHMALLOW_STICK = registerItem("burnt_marshmallow_stick", new Item(new Item.Settings().maxCount(1).food(ModFoodComponents.BURNT_MARSHMALLOW)));

    public static final Item SKULL = registerItem("skull", new Item(new Item.Settings()));
    public static final Item FIRE_SALAMANDER_SCALES = registerItem("fire_salamander_scales", new Item(new Item.Settings().fireproof()));
    public static final Item SPECTRAL_BAT_WING = registerItem("spectral_bat_wing", new Item(new Item.Settings()));
    public static final Item SPECTRAL_BAT_EAR = registerItem("spectral_bat_ear", new Item(new Item.Settings()));
    public static final Item CROCODILE_SCALE = registerItem("crocodile_scale", new Item(new Item.Settings()));
    public static final Item CROCODILE_SCALE_ALBINO = registerItem("crocodile_scale_albino", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item CROCODILE_TOOTH = registerItem("crocodile_tooth", new Item(new Item.Settings()));
    public static final Item WATER_LILY = registerItem("water_lily", new Item(new Item.Settings()));
    public static final Item BARK = registerItem("bark", new Item(new Item.Settings()));
    public static final Item SAWDUST = registerItem("sawdust", new Item(new Item.Settings()));
    public static final Item WARDEN_TENDRIL = registerItem("warden_tendril", new Item(new Item.Settings().rarity(Rarity.RARE)));
    public static final Item SHARK_TOOTH = registerItem("shark_tooth", new Item(new Item.Settings()));
    public static final Item QUILL = registerItem("quill", new QuillItem(new Item.Settings()));
    public static final Item TRUFFLE = registerItem("truffle", new Item(new Item.Settings()));
    public static final Item CONCH_SHELL = registerItem("conch_shell", new ConchShellItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item ARAPAIMA_SCALE_BLACK = registerItem("arapaima_scale_black", new Item(new Item.Settings()));
    public static final Item ARAPAIMA_SCALE_GREEN = registerItem("arapaima_scale_green", new Item(new Item.Settings()));
    public static final Item PEARL = registerItem("pearl", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item COCONUT_SHELL = registerItem("coconut_shell", new Item(new Item.Settings()));
    public static final Item SILK = registerItem("silk", new Item(new Item.Settings()));
    public static final Item NET = registerItem("net", new Item(new Item.Settings()));
    public static final Item UNICORN_DUST = registerItem("unicorn_dust", new UnicornDustItem(new Item.Settings()));

    public static final Item GUIDE_BOOK = registerItem("guide_book", new GuideBookItem(new Item.Settings().maxCount(1)));

    public static final Item SMALL_ANTLER = registerItem("small_antler", new SmallAntlerItem(new Item.Settings()));
    public static final Item MEDIUM_ANTLER = registerItem("medium_antler", new MediumAntlerItem(new Item.Settings()));
    public static final Item LARGE_ANTLER = registerItem("large_antler", new LargeAntlerItem(new Item.Settings()));

    public static final Item RACCOON_TAIL = registerItem("raccoon_tail", new Item(new Item.Settings()));
    public static final Item RACCOON_HAT = registerItem("raccoon_hat", new RaccoonHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));

    public static final Item ADVENTURER_HAT = registerItem("adventurer_hat", new AdventurerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));
    public static final Item SONIC_BLOCKERS = registerItem("sonic_blockers", new SonicBlockersItem(ModArmorMaterials.UTILITY, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(47)).maxCount(1)));
    public static final Item SHAMAN_MASK = registerItem("shaman_mask", new ShamanMaskItem(ModArmorMaterials.UTILITY, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(40)).maxCount(1).rarity(Rarity.UNCOMMON)));

    public static final Item SMALL_ANTLER_HAT = registerItem("small_antler_hat", new SmallAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));
    public static final Item MEDIUM_ANTLER_HAT = registerItem("medium_antler_hat", new MediumAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));
    public static final Item LARGE_ANTLER_HAT = registerItem("large_antler_hat", new LargeAntlerHatItem(ModArmorMaterials.COSMETIC, ArmorItem.Type.HELMET, new Item.Settings().maxCount(1)));

    public static final Item CROCODILE_CHESTPLATE = registerItem("crocodile_chestplate", new CrocodileChestplateItem(ModArmorMaterials.CROCODILE, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(20))));
    public static final Item CROCODILE_CHESTPLATE_ALBINO = registerItem("crocodile_chestplate_albino", new CrocodileChestplateAlbinoItem(ModArmorMaterials.CROCODILE_ALBINO, ArmorItem.Type.CHESTPLATE, new Item.Settings().rarity(Rarity.RARE).maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(20))));
    public static final Item SALAMANDER_HELMET = registerItem("salamander_helmet", new SalamanderHelmetItem(ModArmorMaterials.SALAMANDER, ArmorItem.Type.HELMET, new Item.Settings().fireproof().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(40))));
    public static final Item SALAMANDER_CHESTPLATE = registerItem("salamander_chestplate", new SalamanderChestplateItem(ModArmorMaterials.SALAMANDER, ArmorItem.Type.CHESTPLATE, new Item.Settings().fireproof().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(40))));
    public static final Item SALAMANDER_LEGGINGS = registerItem("salamander_leggings", new SalamanderLeggingsItem(ModArmorMaterials.SALAMANDER, ArmorItem.Type.LEGGINGS, new Item.Settings().fireproof().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(40))));
    public static final Item SALAMANDER_BOOTS = registerItem("salamander_boots", new SalamanderBootsItem(ModArmorMaterials.SALAMANDER, ArmorItem.Type.BOOTS, new Item.Settings().fireproof().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(40))));

    public static final Item ARAPAIMA_HELMET_BLACK = registerItem("arapaima_helmet_black", new ArapaimaHelmetBlackItem(ModArmorMaterials.ARAPAIMA_BLACK, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    public static final Item ARAPAIMA_CHESTPLATE_BLACK = registerItem("arapaima_chestplate_black", new ArapaimaChestplateBlackItem(ModArmorMaterials.ARAPAIMA_BLACK, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    public static final Item ARAPAIMA_LEGGINGS_BLACK = registerItem("arapaima_leggings_black", new ArapaimaLeggingsBlackItem(ModArmorMaterials.ARAPAIMA_BLACK, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(15))));
    public static final Item ARAPAIMA_BOOTS_BLACK = registerItem("arapaima_boots_black", new ArapaimaBootsBlackItem(ModArmorMaterials.ARAPAIMA_BLACK, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(15))));

    public static final Item ARAPAIMA_HELMET_GREEN = registerItem("arapaima_helmet_green", new ArapaimaHelmetGreenItem(ModArmorMaterials.ARAPAIMA_GREEN, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(15))));
    public static final Item ARAPAIMA_CHESTPLATE_GREEN = registerItem("arapaima_chestplate_green", new ArapaimaChestplateGreenItem(ModArmorMaterials.ARAPAIMA_GREEN, ArmorItem.Type.CHESTPLATE, new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(15))));
    public static final Item ARAPAIMA_LEGGINGS_GREEN = registerItem("arapaima_leggings_green", new ArapaimaLeggingsGreenItem(ModArmorMaterials.ARAPAIMA_GREEN, ArmorItem.Type.LEGGINGS, new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(15))));
    public static final Item ARAPAIMA_BOOTS_GREEN = registerItem("arapaima_boots_green", new ArapaimaBootsGreenItem(ModArmorMaterials.ARAPAIMA_GREEN, ArmorItem.Type.BOOTS, new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(15))));

    public static final Item JADE_CROWN = registerItem("jade_crown", new JadeCrownItem(ModArmorMaterials.JADE, ArmorItem.Type.HELMET, new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(25)).rarity(Rarity.UNCOMMON)));

    public static final Item SALAMANDER_UPGRADE_SMITHING_TEMPLATE = registerItem("salamander_upgrade_smithing_template",
            new SalamanderUpgradeSmithingTemplateItem(new Item.Settings()));

    public static final Item BUG_NET = registerItem("bug_net", new BugNetItem(new Item.Settings().maxCount(1).maxDamage(3)));
    public static final Item SKULL_WAND = registerItem("skull_wand", new Item(new Item.Settings().maxCount(1)));
    public static final Item NIGHTMARE_AMULET = registerItem("nightmare_amulet", new NightmareAmuletItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));
    public static final Item COPPER_TRUMPET = registerItem("copper_trumpet", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_1, ModSounds.COPPER_TRUMPET_2));
    public static final Item COPPER_TRUMPET_EXPOSED = registerItem("copper_trumpet_exposed", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_EXPOSED_1, ModSounds.COPPER_TRUMPET_EXPOSED_2));
    public static final Item COPPER_TRUMPET_WEATHERED = registerItem("copper_trumpet_weathered", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_WEATHERED_1, ModSounds.COPPER_TRUMPET_WEATHERED_2));
    public static final Item COPPER_TRUMPET_OXIDIZED = registerItem("copper_trumpet_oxidized", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_OXIDIZED_1, ModSounds.COPPER_TRUMPET_OXIDIZED_2));
    public static final Item WAXED_COPPER_TRUMPET = registerItem("waxed_copper_trumpet", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_1, ModSounds.COPPER_TRUMPET_2));
    public static final Item WAXED_COPPER_TRUMPET_EXPOSED = registerItem("waxed_copper_trumpet_exposed", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_EXPOSED_1, ModSounds.COPPER_TRUMPET_EXPOSED_2));
    public static final Item WAXED_COPPER_TRUMPET_WEATHERED = registerItem("waxed_copper_trumpet_weathered", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_WEATHERED_1, ModSounds.COPPER_TRUMPET_WEATHERED_2));
    public static final Item WAXED_COPPER_TRUMPET_OXIDIZED = registerItem("waxed_copper_trumpet_oxidized", new CopperTrumpetItem(new Item.Settings().maxCount(1), ModSounds.COPPER_TRUMPET_OXIDIZED_1, ModSounds.COPPER_TRUMPET_OXIDIZED_2));

    public static final Item DEAFEN_ICON = registerItem("deafen_icon", new Item(new Item.Settings()));
    public static final Item TAB_ICON = registerItem("tab_icon", new Item(new Item.Settings()));
    public static final Item ERASER = registerItem("eraser", new EraserItem(new Item.Settings().maxCount(1)));

    public static final Item MAGNET = registerItem("magnet", new MagnetItem(new Item.Settings().maxCount(1)));
    public static final Item RAW_MAGNETITE = registerItem("raw_magnetite", new Item(new Item.Settings()));
    public static final Item MAGNETITE_INGOT = registerItem("magnetite_ingot", new Item(new Item.Settings()));

    public static final Item RAW_JADE = registerItem("raw_jade", new Item(new Item.Settings()));
    public static final Item JADE = registerItem("jade", new Item(new Item.Settings()));

    public static final Item MUD_BLOSSOM_SEED = registerItem("mud_blossom_seed", new AliasedBlockItem(ModBlocks.MUD_BLOSSOM, new Item.Settings()));

    public static final Item MACUAHUITL = registerItem("macuahuitl", new MacuahuitlItem(ModToolMaterials.MACUAHUITL, new Item.Settings(), 3, -3.0F));

    public static final Item DEATH_WHISTLE = registerItem("death_whistle", new DeathWhistleItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(DomiXsCreatures.MOD_ID, name), item);
    }

    public static void registerModItems() {
        DomiXsCreatures.LOGGER.info("Registering Items for " + DomiXsCreatures.MOD_ID);
    }
}
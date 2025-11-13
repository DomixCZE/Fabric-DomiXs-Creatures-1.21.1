package net.domixcze.domixscreatures.datagen;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.MudBlossomBlock;
import net.domixcze.domixscreatures.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.FIRE_SALAMANDER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.SPECTRAL_BAT_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.WHALE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.GOLDFISH_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.WISP_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CROCODILE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BEAVER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.IGUANA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.TIGER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.DEER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MOOSE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.SHARK_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.EEL_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.HIPPO_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.SHAMAN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.VINE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MUD_GOLEM_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.MOLE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.WORM_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.PORCUPINE_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.WATER_STRIDER_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BOAR_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BISON_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.SUN_BEAR_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CATERPILLAR_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BUTTERFLY_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.GORILLA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.PIRANHA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.NEON_TETRA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.PEACOCK_BASS_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.BETTA_FISH_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ARAPAIMA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ANGLERFISH_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.FRESHWATER_STINGRAY_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CHEETAH_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.HERMIT_CRAB_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.RACCOON_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CAPYBARA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.HYENA_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.ANCIENT_SKELETON_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.UNICORN_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.CATFISH_SPAWN_EGG,
                new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

        itemModelGenerator.register(ModItems.FIRE_SALAMANDER_SCALES, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_BAT_WING, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_BAT_EAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLDFISH_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.PIRANHA_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.NEON_TETRA_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.PEACOCK_BASS_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.BETTA_FISH_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.ANGLERFISH_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.FRESHWATER_STINGRAY_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SKULL, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_SCALE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_SCALE_ALBINO, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_TOOTH, Models.GENERATED);
        itemModelGenerator.register(ModItems.WATER_LILY, Models.GENERATED);
        itemModelGenerator.register(ModItems.BARK, Models.GENERATED);
        itemModelGenerator.register(ModItems.SAWDUST, Models.GENERATED);
        itemModelGenerator.register(ModItems.WARDEN_TENDRIL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHARK_TOOTH, Models.GENERATED);
        itemModelGenerator.register(ModItems.QUILL, Models.GENERATED);
        itemModelGenerator.register(ModItems.TRUFFLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_SCALE_BLACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_SCALE_GREEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.PEARL, Models.GENERATED);
        itemModelGenerator.register(ModItems.COCONUT_SHELL, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILK, Models.GENERATED);
        itemModelGenerator.register(ModItems.NET, Models.GENERATED);
        itemModelGenerator.register(ModItems.UNICORN_DUST, Models.GENERATED);

        itemModelGenerator.register(ModItems.GOLDFISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.WORM, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_FRUIT, Models.GENERATED);
        itemModelGenerator.register(ModItems.HONEYED_APPLE, Models.GENERATED);
        itemModelGenerator.register(ModItems.BANANA, Models.GENERATED);
        itemModelGenerator.register(ModItems.COOKED_BISON_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_BISON_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COOKED_DEER_VENISON, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_DEER_VENISON, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_EEL_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COOKED_EEL_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.COCONUT_SLICE, Models.GENERATED);

        itemModelGenerator.register(ModItems.MARSHMALLOW, Models.GENERATED);

        itemModelGenerator.register(ModItems.SMALL_ANTLER, Models.GENERATED);
        itemModelGenerator.register(ModItems.MEDIUM_ANTLER, Models.GENERATED);
        itemModelGenerator.register(ModItems.LARGE_ANTLER, Models.GENERATED);

        itemModelGenerator.register(ModItems.RACCOON_TAIL, Models.GENERATED);
        itemModelGenerator.register(ModItems.RACCOON_HAT, Models.GENERATED);

        itemModelGenerator.register(ModItems.SMALL_ANTLER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.MEDIUM_ANTLER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.LARGE_ANTLER_HAT, Models.GENERATED);

        itemModelGenerator.register(ModItems.ADVENTURER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SONIC_BLOCKERS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHAMAN_MASK, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_CHESTPLATE_ALBINO, Models.GENERATED);

        itemModelGenerator.register(ModItems.ARAPAIMA_HELMET_GREEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_CHESTPLATE_GREEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_LEGGINGS_GREEN, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_BOOTS_GREEN, Models.GENERATED);

        itemModelGenerator.register(ModItems.ARAPAIMA_HELMET_BLACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_CHESTPLATE_BLACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_LEGGINGS_BLACK, Models.GENERATED);
        itemModelGenerator.register(ModItems.ARAPAIMA_BOOTS_BLACK, Models.GENERATED);

        itemModelGenerator.register(ModItems.JADE_CROWN, Models.GENERATED);

        itemModelGenerator.register(ModItems.SALAMANDER_HELMET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_BOOTS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);

        itemModelGenerator.register(ModItems.TAB_ICON, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEAFEN_ICON, Models.GENERATED);
        itemModelGenerator.register(ModItems.ERASER, Models.GENERATED);

        itemModelGenerator.register(ModItems.NIGHTMARE_AMULET, Models.GENERATED);

        itemModelGenerator.register(ModItems.RAW_MAGNETITE, Models.GENERATED);
        itemModelGenerator.register(ModItems.MAGNETITE_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_JADE, Models.GENERATED);
        itemModelGenerator.register(ModItems.JADE, Models.GENERATED);

        itemModelGenerator.register(ModItems.GUIDE_BOOK, Models.GENERATED);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool spectralPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SPECTRAL_PLANKS);
        BlockStateModelGenerator.BlockTexturePool palmPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.PALM_PLANKS);

        BlockStateModelGenerator.BlockTexturePool limestonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.LIMESTONE_BRICKS);
        BlockStateModelGenerator.BlockTexturePool mossyLimestonePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.MOSSY_LIMESTONE_BRICKS);

        limestonePool.stairs(ModBlocks.LIMESTONE_BRICK_STAIRS);
        limestonePool.slab(ModBlocks.LIMESTONE_BRICK_SLAB);
        limestonePool.wall(ModBlocks.LIMESTONE_BRICK_WALL);
        mossyLimestonePool.stairs(ModBlocks.MOSSY_LIMESTONE_BRICK_STAIRS);
        mossyLimestonePool.slab(ModBlocks.MOSSY_LIMESTONE_BRICK_SLAB);
        mossyLimestonePool.wall(ModBlocks.MOSSY_LIMESTONE_BRICK_WALL);

        spectralPool.stairs(ModBlocks.SPECTRAL_STAIRS);
        spectralPool.slab(ModBlocks.SPECTRAL_SLAB);
        spectralPool.fence(ModBlocks.SPECTRAL_FENCE);
        spectralPool.fenceGate(ModBlocks.SPECTRAL_FENCE_GATE);
        spectralPool.button(ModBlocks.SPECTRAL_BUTTON);
        spectralPool.pressurePlate(ModBlocks.SPECTRAL_PRESSURE_PLATE);
        blockStateModelGenerator.registerDoor(ModBlocks.SPECTRAL_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.SPECTRAL_TRAPDOOR);

        blockStateModelGenerator.registerLog(ModBlocks.SPECTRAL_LOG).log(ModBlocks.SPECTRAL_LOG).wood(ModBlocks.SPECTRAL_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_SPECTRAL_LOG).log(ModBlocks.STRIPPED_SPECTRAL_LOG).wood(ModBlocks.STRIPPED_SPECTRAL_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SPECTRAL_LEAVES);

        blockStateModelGenerator.registerTintableCross(ModBlocks.SPECTRAL_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);

        palmPool.stairs(ModBlocks.PALM_STAIRS);
        palmPool.slab(ModBlocks.PALM_SLAB);
        palmPool.fence(ModBlocks.PALM_FENCE);
        palmPool.fenceGate(ModBlocks.PALM_FENCE_GATE);
        palmPool.button(ModBlocks.PALM_BUTTON);
        palmPool.pressurePlate(ModBlocks.PALM_PRESSURE_PLATE);
        blockStateModelGenerator.registerDoor(ModBlocks.PALM_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.PALM_TRAPDOOR);

        blockStateModelGenerator.registerLog(ModBlocks.PALM_LOG).log(ModBlocks.PALM_LOG).wood(ModBlocks.PALM_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_PALM_LOG).log(ModBlocks.STRIPPED_PALM_LOG).wood(ModBlocks.STRIPPED_PALM_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PALM_LEAVES);

        blockStateModelGenerator.registerTintableCross(ModBlocks.PALM_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CRACKED_GLASS_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PEARL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLOCK_OF_MAGNETITE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BLOCK_OF_JADE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGNETITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_MAGNETITE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.JADE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_JADE_ORE);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.LIMESTONE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.COBBLED_LIMESTONE);

        blockStateModelGenerator.registerCrop(ModBlocks.MUD_BLOSSOM, MudBlossomBlock.AGE, 0, 1, 2, 3);
    }
}
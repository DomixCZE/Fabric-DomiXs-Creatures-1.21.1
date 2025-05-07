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

        itemModelGenerator.register(ModItems.FIRE_SALAMANDER_SCALES, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_BAT_WING, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_BAT_EAR, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLDFISH_BUCKET, Models.GENERATED);
        itemModelGenerator.register(ModItems.GOLDFISH, Models.GENERATED);
        itemModelGenerator.register(ModItems.WORM, Models.GENERATED);
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

        itemModelGenerator.register(ModItems.SMALL_ANTLER, Models.GENERATED);
        itemModelGenerator.register(ModItems.MEDIUM_ANTLER, Models.GENERATED);
        itemModelGenerator.register(ModItems.LARGE_ANTLER, Models.GENERATED);

        itemModelGenerator.register(ModItems.SMALL_ANTLER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.MEDIUM_ANTLER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.LARGE_ANTLER_HAT, Models.GENERATED);

        itemModelGenerator.register(ModItems.ADVENTURER_HAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SONIC_BLOCKERS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHAMAN_MASK, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.CROCODILE_CHESTPLATE_ALBINO, Models.GENERATED);

        itemModelGenerator.register(ModItems.SALAMANDER_HELMET, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_CHESTPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_LEGGINGS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_BOOTS, Models.GENERATED);
        itemModelGenerator.register(ModItems.SALAMANDER_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);

        itemModelGenerator.register(ModItems.TAB_ICON, Models.GENERATED);
        itemModelGenerator.register(ModItems.DEAFEN_ICON, Models.GENERATED);
        itemModelGenerator.register(ModItems.SPECTRAL_FRUIT, Models.GENERATED);

        itemModelGenerator.register(ModItems.NIGHTMARE_AMULET, Models.GENERATED);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool spectralPool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.SPECTRAL_PLANKS);

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

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CRACKED_GLASS_BLOCK);

        blockStateModelGenerator.registerCrop(ModBlocks.MUD_BLOSSOM, MudBlossomBlock.AGE, 0, 1, 2, 3);
    }
}
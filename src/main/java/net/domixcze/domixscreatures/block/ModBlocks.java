package net.domixcze.domixscreatures.block;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.custom.BarnacleBlock;
import net.domixcze.domixscreatures.block.custom.CrackedGlassBlock;
import net.domixcze.domixscreatures.block.custom.CrocodileEggBlock;
import net.domixcze.domixscreatures.block.custom.PileOfSticksBlock;
import net.domixcze.domixscreatures.world.tree.SpectralSaplingGenerator;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CRACKED_GLASS_BLOCK = registerBlock("cracked_glass_block",
            new CrackedGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).sounds(BlockSoundGroup.GLASS)));

    public static final Block BARNACLE_BLOCK = registerBlock("barnacle_block",
            new BarnacleBlock(FabricBlockSettings.create().strength(1.0f).sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block CROCODILE_EGG = registerBlock("crocodile_egg",
            new CrocodileEggBlock(FabricBlockSettings.create().strength(0.5F).ticksRandomly().sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block PILE_OF_STICKS_BLOCK = registerBlock("pile_of_sticks_block",
            new PileOfSticksBlock(FabricBlockSettings.create().sounds(BlockSoundGroup.ROOTS).nonOpaque()));

    public static final Block SPECTRAL_LOG = registerBlock("spectral_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG)));
    public static final Block SPECTRAL_WOOD = registerBlock("spectral_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_SPECTRAL_LOG = registerBlock("stripped_spectral_log",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_SPECTRAL_WOOD = registerBlock("stripped_spectral_wood",
            new PillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block SPECTRAL_PLANKS = registerBlock("spectral_planks",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block SPECTRAL_LEAVES = registerBlock("spectral_leaves",
            new LeavesBlock(FabricBlockSettings.copyOf(Blocks.AZALEA_LEAVES).nonOpaque()));

    public static final Block SPECTRAL_STAIRS = registerBlock("spectral_stairs",
            new StairsBlock(ModBlocks.SPECTRAL_PLANKS.getDefaultState(), FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block SPECTRAL_SLAB = registerBlock("spectral_slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));

    public static final Block SPECTRAL_FENCE = registerBlock("spectral_fence",
            new FenceBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block SPECTRAL_FENCE_GATE = registerBlock("spectral_fence_gate",
            new FenceGateBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(2.0F, 3.0F), WoodType.OAK));

    public static final Block SPECTRAL_BUTTON = registerBlock("spectral_button",
            new ButtonBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(0.5F), BlockSetType.CHERRY, 10, true));
    public static final Block SPECTRAL_PRESSURE_PLATE = registerBlock("spectral_pressure_plate",
            new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING,
                    FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(0.5F), BlockSetType.CHERRY));

    public static final Block SPECTRAL_DOOR = registerBlock("spectral_door",
            new DoorBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).strength(3.0F).nonOpaque(), BlockSetType.CHERRY));
    public static final Block SPECTRAL_TRAPDOOR = registerBlock("spectral_trapdoor",
            new TrapdoorBlock(FabricBlockSettings.copyOf(Blocks.CHERRY_TRAPDOOR).strength(3.0F), BlockSetType.CHERRY));

    public static final Block SPECTRAL_SAPLING = registerBlock("spectral_sapling",
            new SaplingBlock(new SpectralSaplingGenerator(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(DomiXsCreatures.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(DomiXsCreatures.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        DomiXsCreatures.LOGGER.info("Registering Blocks for " + DomiXsCreatures.MOD_ID);
    }
}
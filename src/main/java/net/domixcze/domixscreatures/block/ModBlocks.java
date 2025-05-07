package net.domixcze.domixscreatures.block;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.custom.*;
import net.domixcze.domixscreatures.world.tree.ModSaplingGenerators;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CRACKED_GLASS_BLOCK = registerBlock("cracked_glass_block",
            new CrackedGlassBlock(AbstractBlock.Settings.copy(Blocks.GLASS).sounds(BlockSoundGroup.GLASS)));

    public static final Block BARNACLE_BLOCK = registerBlock("barnacle_block",
            new BarnacleBlock(AbstractBlock.Settings.create().strength(1.0f).sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block CROCODILE_EGG = registerBlock("crocodile_egg",
            new CrocodileEggBlock(AbstractBlock.Settings.create().strength(0.5F).ticksRandomly().sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block PILE_OF_STICKS_BLOCK = registerBlock("pile_of_sticks_block",
            new PileOfSticksBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.ROOTS).nonOpaque()));

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new SawdustBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.1f).nonOpaque()));

    public static final Block SPECTRAL_LOG = registerBlock("spectral_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block SPECTRAL_WOOD = registerBlock("spectral_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_SPECTRAL_LOG = registerBlock("stripped_spectral_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_SPECTRAL_WOOD = registerBlock("stripped_spectral_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block SPECTRAL_PLANKS = registerBlock("spectral_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block SPECTRAL_LEAVES = registerBlock("spectral_leaves",
            new LeavesBlock(AbstractBlock.Settings.copy(Blocks.AZALEA_LEAVES).nonOpaque()));

    public static final Block SPECTRAL_STAIRS = registerBlock("spectral_stairs",
            new StairsBlock(ModBlocks.SPECTRAL_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block SPECTRAL_SLAB = registerBlock("spectral_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));

    public static final Block SPECTRAL_FENCE = registerBlock("spectral_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block SPECTRAL_FENCE_GATE = registerBlock("spectral_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));

    public static final Block SPECTRAL_BUTTON = registerBlock("spectral_button",
            new ButtonBlock(BlockSetType.CHERRY, 10, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F)));
    public static final Block SPECTRAL_PRESSURE_PLATE = registerBlock("spectral_pressure_plate",
            new PressurePlateBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F)));

    public static final Block SPECTRAL_DOOR = registerBlock("spectral_door",
            new DoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(3.0F).nonOpaque()));
    public static final Block SPECTRAL_TRAPDOOR = registerBlock("spectral_trapdoor",
            new TrapdoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.CHERRY_TRAPDOOR).strength(3.0F)));

    public static final Block SPECTRAL_SAPLING = registerBlock("spectral_sapling",
            new SaplingBlock(ModSaplingGenerators.SPECTRAL, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING)));

    public static final Block MUD_BLOSSOM = Registry.register(Registries.BLOCK, Identifier.of(DomiXsCreatures.MOD_ID, "mud_blossom"),
            new MudBlossomBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));

    public static final Block MOLEHILL_BLOCK = registerBlock("molehill_block",
            new MolehillBlock(AbstractBlock.Settings.copy(Blocks.DIRT).nonOpaque()));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(DomiXsCreatures.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(DomiXsCreatures.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        DomiXsCreatures.LOGGER.info("Registering Blocks for " + DomiXsCreatures.MOD_ID);
    }
}
package net.domixcze.domixscreatures.block;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.custom.*;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.world.tree.ModSaplingGenerators;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {
    public static final Block CRACKED_GLASS_BLOCK = registerBlock("cracked_glass_block",
            new CrackedGlassBlock(AbstractBlock.Settings.copy(Blocks.GLASS).sounds(BlockSoundGroup.GLASS)));

    public static final Block BARNACLE_BLOCK = registerBlock("barnacle_block",
            new BarnacleBlock(AbstractBlock.Settings.create().strength(1.0f).requiresTool().sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block CROCODILE_EGG = registerBlock("crocodile_egg",
            new CrocodileEggBlock(AbstractBlock.Settings.create().strength(0.5F).ticksRandomly().sounds(BlockSoundGroup.POINTED_DRIPSTONE)));

    public static final Block PILE_OF_STICKS_BLOCK = registerBlock("pile_of_sticks_block",
            new PileOfSticksBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.ROOTS).nonOpaque()));

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new SawdustBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.SAND).strength(0.1f).nonOpaque()));

    public static final Block PEARL_BLOCK = registerBlock("pearl_block",
            new Block(AbstractBlock.Settings.create().strength(1.5f).requiresTool().sounds(BlockSoundGroup.STONE)));

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
            new SpectralLeavesBlock(AbstractBlock.Settings.create().strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.AZALEA_LEAVES).nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).burnable().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never)));

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
            new ModSaplingBlock(ModSaplingGenerators.SPECTRAL, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), ModTags.Blocks.SPECTRAL_TREE_CAN_GROW_ON));

    public static final Block PALM_LOG = registerBlock("palm_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block PALM_WOOD = registerBlock("palm_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_PALM_LOG = registerBlock("stripped_palm_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_PALM_WOOD = registerBlock("stripped_palm_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block PALM_PLANKS = registerBlock("palm_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block PALM_LEAVES = registerBlock("palm_leaves",
            new LeavesBlock(AbstractBlock.Settings.create().strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.AZALEA_LEAVES).nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).burnable().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never)));

    public static final Block PALM_STAIRS = registerBlock("palm_stairs",
            new StairsBlock(ModBlocks.PALM_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block PALM_SLAB = registerBlock("palm_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));

    public static final Block PALM_FENCE = registerBlock("palm_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));
    public static final Block PALM_FENCE_GATE = registerBlock("palm_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F)));

    public static final Block PALM_BUTTON = registerBlock("palm_button",
            new ButtonBlock(BlockSetType.CHERRY, 10, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F)));
    public static final Block PALM_PRESSURE_PLATE = registerBlock("palm_pressure_plate",
            new PressurePlateBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F)));

    public static final Block PALM_DOOR = registerBlock("palm_door",
            new DoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(3.0F).nonOpaque()));
    public static final Block PALM_TRAPDOOR = registerBlock("palm_trapdoor",
            new TrapdoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.CHERRY_TRAPDOOR).strength(3.0F)));

    public static final Block PALM_SAPLING = registerBlock("palm_sapling",
            new ModSaplingBlock(ModSaplingGenerators.PALM, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), ModTags.Blocks.PALM_CAN_GROW_ON));

    public static final Block MUD_BLOSSOM = Registry.register(Registries.BLOCK, Identifier.of(DomiXsCreatures.MOD_ID, "mud_blossom"),
            new MudBlossomBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));

    public static final Block MOLEHILL_BLOCK = registerBlock("molehill_block",
            new MolehillBlock(AbstractBlock.Settings.copy(Blocks.DIRT).nonOpaque()));

    public static final Block MAGNETITE_ORE = registerBlock("magnetite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(3f).requiresTool()));
    public static final Block DEEPSLATE_MAGNETITE_ORE = registerBlock("deepslate_magnetite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block POSITIVE_MAGNET_BLOCK = registerBlock("positive_magnet_block",
            new PositiveMagnetBlock(AbstractBlock.Settings.create().strength(3.0f).requiresTool()));
    public static final Block NEGATIVE_MAGNET_BLOCK = registerBlock("negative_magnet_block",
            new NegativeMagnetBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).strength(3.0f).requiresTool()));

    public static final Block COCONUT_BLOCK = registerBlock("coconut_block",
            new CoconutBlock(AbstractBlock.Settings.create().strength(1.5f).nonOpaque().sounds(BlockSoundGroup.WOOD)));


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
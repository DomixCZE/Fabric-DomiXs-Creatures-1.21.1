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
            new PileOfSticksBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).sounds(BlockSoundGroup.ROOTS).nonOpaque()));

    public static final Block SAWDUST_BLOCK = registerBlock("sawdust_block",
            new SawdustBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).sounds(BlockSoundGroup.SAND).strength(0.1f).nonOpaque()));

    public static final Block PEARL_BLOCK = registerBlock("pearl_block",
            new Block(AbstractBlock.Settings.create().strength(1.5f).requiresTool().mapColor(MapColor.PINK).sounds(BlockSoundGroup.STONE)));

    public static final Block SPECTRAL_LOG = registerBlock("spectral_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_WOOD = registerBlock("spectral_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block STRIPPED_SPECTRAL_LOG = registerBlock("stripped_spectral_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block STRIPPED_SPECTRAL_WOOD = registerBlock("stripped_spectral_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_PLANKS = registerBlock("spectral_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_LEAVES = registerBlock("spectral_leaves",
            new SpectralLeavesBlock(AbstractBlock.Settings.create().strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.AZALEA_LEAVES).nonOpaque()
                    .allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never).blockVision(Blocks::never).burnable()
                    .pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never).mapColor(MapColor.LIGHT_BLUE)));

    public static final Block SPECTRAL_STAIRS = registerBlock("spectral_stairs",
            new StairsBlock(ModBlocks.SPECTRAL_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_SLAB = registerBlock("spectral_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.LIGHT_BLUE)));

    public static final Block SPECTRAL_FENCE = registerBlock("spectral_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_FENCE_GATE = registerBlock("spectral_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.LIGHT_BLUE)));

    public static final Block SPECTRAL_BUTTON = registerBlock("spectral_button",
            new ButtonBlock(BlockSetType.CHERRY, 10, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F).mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_PRESSURE_PLATE = registerBlock("spectral_pressure_plate",
            new PressurePlateBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F).mapColor(MapColor.LIGHT_BLUE)));

    public static final Block SPECTRAL_DOOR = registerBlock("spectral_door",
            new DoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(3.0F).nonOpaque().mapColor(MapColor.LIGHT_BLUE)));
    public static final Block SPECTRAL_TRAPDOOR = registerBlock("spectral_trapdoor",
            new TrapdoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.CHERRY_TRAPDOOR).strength(3.0F).mapColor(MapColor.LIGHT_BLUE)));

    public static final Block SPECTRAL_SAPLING = registerBlock("spectral_sapling",
            new ModSaplingBlock(ModSaplingGenerators.SPECTRAL, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), ModTags.Blocks.SPECTRAL_TREE_CAN_GROW_ON));

    public static final Block PALM_LOG = registerBlock("palm_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_WOOD = registerBlock("palm_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block STRIPPED_PALM_LOG = registerBlock("stripped_palm_log",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block STRIPPED_PALM_WOOD = registerBlock("stripped_palm_wood",
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_PLANKS = registerBlock("palm_planks",
            new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_LEAVES = registerBlock("palm_leaves",
            new LeavesBlock(AbstractBlock.Settings.create().strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.AZALEA_LEAVES)
                    .nonOpaque().allowsSpawning(Blocks::canSpawnOnLeaves).suffocates(Blocks::never)
                    .blockVision(Blocks::never).burnable().pistonBehavior(PistonBehavior.DESTROY).solidBlock(Blocks::never).mapColor(MapColor.DARK_GREEN)));

    public static final Block PALM_STAIRS = registerBlock("palm_stairs",
            new StairsBlock(ModBlocks.PALM_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_SLAB = registerBlock("palm_slab",
            new SlabBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.TERRACOTTA_ORANGE)));

    public static final Block PALM_FENCE = registerBlock("palm_fence",
            new FenceBlock(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_FENCE_GATE = registerBlock("palm_fence_gate",
            new FenceGateBlock(WoodType.OAK, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).mapColor(MapColor.TERRACOTTA_ORANGE)));

    public static final Block PALM_BUTTON = registerBlock("palm_button",
            new ButtonBlock(BlockSetType.CHERRY, 10, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F).mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_PRESSURE_PLATE = registerBlock("palm_pressure_plate",
            new PressurePlateBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(0.5F).mapColor(MapColor.TERRACOTTA_ORANGE)));

    public static final Block PALM_DOOR = registerBlock("palm_door",
            new DoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.OAK_PLANKS).strength(3.0F).nonOpaque().mapColor(MapColor.TERRACOTTA_ORANGE)));
    public static final Block PALM_TRAPDOOR = registerBlock("palm_trapdoor",
            new TrapdoorBlock(BlockSetType.CHERRY, AbstractBlock.Settings.copy(Blocks.CHERRY_TRAPDOOR).strength(3.0F).mapColor(MapColor.TERRACOTTA_ORANGE)));

    public static final Block PALM_SAPLING = registerBlock("palm_sapling",
            new ModSaplingBlock(ModSaplingGenerators.PALM, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), ModTags.Blocks.PALM_CAN_GROW_ON));

    public static final Block MUD_BLOSSOM = Registry.register(Registries.BLOCK, Identifier.of(DomiXsCreatures.MOD_ID, "mud_blossom"),
            new MudBlossomBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)));

    public static final Block MOLEHILL_BLOCK = registerBlock("molehill_block",
            new MolehillBlock(AbstractBlock.Settings.copy(Blocks.DIRT).nonOpaque()));

    public static final Block COCONUT_BLOCK = registerBlock("coconut_block",
            new CoconutBlock(AbstractBlock.Settings.create().strength(1.5f).nonOpaque().sounds(BlockSoundGroup.WOOD)));

    public static final Block MAGNETITE_ORE = registerBlock("magnetite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(3f).requiresTool().mapColor(MapColor.STONE_GRAY)));
    public static final Block DEEPSLATE_MAGNETITE_ORE = registerBlock("deepslate_magnetite_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(4f).requiresTool().mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block JADE_ORE = registerBlock("jade_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(3f).requiresTool().mapColor(MapColor.STONE_GRAY)));
    public static final Block DEEPSLATE_JADE_ORE = registerBlock("deepslate_jade_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(0, 2),
                    AbstractBlock.Settings.create().strength(4f).requiresTool().mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block POSITIVE_MAGNET_BLOCK = registerBlock("positive_magnet_block",
            new PositiveMagnetBlock(AbstractBlock.Settings.create().strength(3.0f).requiresTool().mapColor(MapColor.RED)));
    public static final Block NEGATIVE_MAGNET_BLOCK = registerBlock("negative_magnet_block",
            new NegativeMagnetBlock(AbstractBlock.Settings.create().strength(3.0f).requiresTool().mapColor(MapColor.BLUE)));

    public static final Block BLOCK_OF_MAGNETITE = registerBlock("block_of_magnetite",
            new Block(AbstractBlock.Settings.create().strength(2.5f).requiresTool().mapColor(MapColor.DEEPSLATE_GRAY).sounds(BlockSoundGroup.METAL)));
    public static final Block BLOCK_OF_JADE = registerBlock("block_of_jade",
            new Block(AbstractBlock.Settings.create().strength(2.5f).requiresTool().mapColor(MapColor.PALE_GREEN).sounds(BlockSoundGroup.METAL)));

    public static final Block FISH_TRAP_BLOCK = registerBlock("fish_trap_block",
            new FishTrapBlock(AbstractBlock.Settings.create().strength(1.5F).nonOpaque().mapColor(MapColor.OAK_TAN).sounds(BlockSoundGroup.WOOD)));

    public static final Block SPIKE_TRAP_BLOCK = registerBlock("spike_trap_block",
            new SpikeTrapBlock(AbstractBlock.Settings.create().strength(3.0f).mapColor(MapColor.STONE_GRAY).requiresTool()));

    public static final Block LIMESTONE = registerBlock("limestone",
            new Block(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block COBBLED_LIMESTONE = registerBlock("cobbled_limestone",
            new Block(AbstractBlock.Settings.create().strength(2.0F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block CHISELED_LIMESTONE = registerBlock("chiseled_limestone",
            new Block(AbstractBlock.Settings.create().strength(1.0F, 3.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block LIMESTONE_BRICKS = registerBlock("limestone_bricks",
            new Block(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block LIMESTONE_STATUE = registerBlock("limestone_statue",
            new LimestoneStatueBlock(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block LIMESTONE_BRICK_STAIRS = registerBlock("limestone_brick_stairs",
            new StairsBlock(ModBlocks.LIMESTONE_BRICKS.getDefaultState(), AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block LIMESTONE_BRICK_SLAB = registerBlock("limestone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block LIMESTONE_BRICK_WALL = registerBlock("limestone_brick_wall",
            new WallBlock(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block MOSSY_LIMESTONE_BRICKS = registerBlock("mossy_limestone_bricks",
            new Block(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block MOSSY_LIMESTONE_BRICK_STAIRS = registerBlock("mossy_limestone_brick_stairs",
            new StairsBlock(ModBlocks.MOSSY_LIMESTONE_BRICKS.getDefaultState(), AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block MOSSY_LIMESTONE_BRICK_SLAB = registerBlock("mossy_limestone_brick_slab",
            new SlabBlock(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block MOSSY_LIMESTONE_BRICK_WALL = registerBlock("mossy_limestone_brick_wall",
            new WallBlock(AbstractBlock.Settings.create().strength(1.5F, 6.0F).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));

    public static final Block PUSHABLE_STATUE_BLOCK = registerBlock("pushable_statue_block",
            new PushableStatueBlock(AbstractBlock.Settings.create().strength(2.0f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block ACTIVATOR_TRACK_BLOCK = registerBlock("activator_track_block",
            new ActivatorTrackBlock(AbstractBlock.Settings.create().strength(1.5f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block TRACK_BLOCK = registerBlock("track_block",
            new TrackBlock(AbstractBlock.Settings.create().strength(1.5f).requiresTool().sounds(BlockSoundGroup.STONE)));

    public static final Block ANCIENT_CHEST_BLOCK = registerBlock("ancient_chest_block",
            new AncientChestBlock(AbstractBlock.Settings.create().nonOpaque().strength(1.0f).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));
    public static final Block CURSED_ANCIENT_CHEST_BLOCK = registerBlock("cursed_ancient_chest_block",
            new CursedAncientChestBlock(AbstractBlock.Settings.create().nonOpaque().strength(1.0f).requiresTool().mapColor(MapColor.PALE_YELLOW).sounds(BlockSoundGroup.STONE)));

    public static final Block WHITE_NET_BLOCK = registerBlock("white_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_GRAY_NET_BLOCK = registerBlock("light_gray_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.LIGHT_GRAY).sounds(BlockSoundGroup.WOOL)));
    public static final Block GRAY_NET_BLOCK = registerBlock("gray_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLACK_NET_BLOCK = registerBlock("black_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.WOOL)));
    public static final Block GREEN_NET_BLOCK = registerBlock("green_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIME_NET_BLOCK = registerBlock("lime_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.LIME).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLUE_NET_BLOCK = registerBlock("blue_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.BLUE).sounds(BlockSoundGroup.WOOL)));
    public static final Block CYAN_NET_BLOCK = registerBlock("cyan_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.CYAN).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_BLUE_NET_BLOCK = registerBlock("light_blue_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.WOOL)));
    public static final Block PURPLE_NET_BLOCK = registerBlock("purple_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.PURPLE).sounds(BlockSoundGroup.WOOL)));
    public static final Block MAGENTA_NET_BLOCK = registerBlock("magenta_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.MAGENTA).sounds(BlockSoundGroup.WOOL)));
    public static final Block PINK_NET_BLOCK = registerBlock("pink_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.PINK).sounds(BlockSoundGroup.WOOL)));
    public static final Block YELLOW_NET_BLOCK = registerBlock("yellow_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.WOOL)));
    public static final Block ORANGE_NET_BLOCK = registerBlock("orange_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.WOOL)));
    public static final Block RED_NET_BLOCK = registerBlock("red_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.RED).sounds(BlockSoundGroup.WOOL)));
    public static final Block BROWN_NET_BLOCK = registerBlock("brown_net_block",
            new NetBlock(AbstractBlock.Settings.create().strength(0.2f).nonOpaque().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL)));

    public static final Block RED_TABLE_CLOTH_BLOCK = registerBlock("red_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.RED).sounds(BlockSoundGroup.WOOL)));
    public static final Block ORANGE_TABLE_CLOTH_BLOCK = registerBlock("orange_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.ORANGE).sounds(BlockSoundGroup.WOOL)));
    public static final Block YELLOW_TABLE_CLOTH_BLOCK = registerBlock("yellow_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.YELLOW).sounds(BlockSoundGroup.WOOL)));
    public static final Block GREEN_TABLE_CLOTH_BLOCK = registerBlock("green_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.GREEN).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIME_TABLE_CLOTH_BLOCK = registerBlock("lime_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.LIME).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLUE_TABLE_CLOTH_BLOCK = registerBlock("blue_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.BLUE).sounds(BlockSoundGroup.WOOL)));
    public static final Block CYAN_TABLE_CLOTH_BLOCK = registerBlock("cyan_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.CYAN).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_BLUE_TABLE_CLOTH_BLOCK = registerBlock("light_blue_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.WOOL)));
    public static final Block PURPLE_TABLE_CLOTH_BLOCK = registerBlock("purple_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.PURPLE).sounds(BlockSoundGroup.WOOL)));
    public static final Block MAGENTA_TABLE_CLOTH_BLOCK = registerBlock("magenta_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.MAGENTA).sounds(BlockSoundGroup.WOOL)));
    public static final Block PINK_TABLE_CLOTH_BLOCK = registerBlock("pink_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.PINK).sounds(BlockSoundGroup.WOOL)));
    public static final Block BROWN_TABLE_CLOTH_BLOCK = registerBlock("brown_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.BROWN).sounds(BlockSoundGroup.WOOL)));
    public static final Block BLACK_TABLE_CLOTH_BLOCK = registerBlock("black_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.BLACK).sounds(BlockSoundGroup.WOOL)));
    public static final Block GRAY_TABLE_CLOTH_BLOCK = registerBlock("gray_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.GRAY).sounds(BlockSoundGroup.WOOL)));
    public static final Block LIGHT_GRAY_TABLE_CLOTH_BLOCK = registerBlock("light_gray_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.LIGHT_GRAY).sounds(BlockSoundGroup.WOOL)));
    public static final Block WHITE_TABLE_CLOTH_BLOCK = registerBlock("white_table_cloth_block",
            new TableClothBlock(AbstractBlock.Settings.create().strength(0.1f).nonOpaque().mapColor(MapColor.WHITE).sounds(BlockSoundGroup.WOOL)));

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
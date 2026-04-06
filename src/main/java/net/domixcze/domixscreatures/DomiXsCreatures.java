package net.domixcze.domixscreatures;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.entity.ModBlockEntities;
import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.*;
import net.domixcze.domixscreatures.item.ModItemGroups;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.potion.ModPotions;
import net.domixcze.domixscreatures.recipe.ModRecipes;
import net.domixcze.domixscreatures.screen.ModScreenHandlers;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.structures.ModStructures;
import net.domixcze.domixscreatures.util.*;
import net.domixcze.domixscreatures.world.RatSpawner;
import net.domixcze.domixscreatures.world.gen.ModEntityGeneration;
import net.domixcze.domixscreatures.world.gen.ModFeatures;
import net.domixcze.domixscreatures.world.gen.ModWorldGeneration;
import net.domixcze.domixscreatures.world.tree.ModFoliagePlacerTypes;
import net.domixcze.domixscreatures.world.tree.ModTrunkPlacerTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.potion.Potions;
import net.minecraft.world.Difficulty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.domixcze.domixscreatures.entity.custom.TermiteEntity.HOLLOW_LOGS;

public class DomiXsCreatures implements ModInitializer {
	public static final String MOD_ID = "domixs-creatures";
    public static final Logger LOGGER = LoggerFactory.getLogger(DomiXsCreatures.MOD_ID);

	@Override
	public void onInitialize() {
		ModGameRules.register();

		FabricDefaultAttributeRegistry.register(ModEntities.FIRE_SALAMANDER, FireSalamanderEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SPECTRAL_BAT, SpectralBatEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WHALE, WhaleEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.GOLDFISH, GoldfishEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WISP, WispEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CROCODILE, CrocodileEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BEAVER, BeaverEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.IGUANA, IguanaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TIGER, TigerEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.DEER, DeerEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MOOSE, MooseEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SHARK, SharkEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.EEL, EelEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MUD_GOLEM, MudGolemEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HIPPO, HippoEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SHAMAN, ShamanEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.VINE, VineEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MOLE, MoleEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WORM, WormEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PORCUPINE, PorcupineEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.WATER_STRIDER, WaterStriderEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BOAR, BoarEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BISON, BisonEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SUN_BEAR, SunBearEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BUTTERFLY, ButterflyEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CATERPILLAR, CaterpillarEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.GORILLA, GorillaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PIRANHA, PiranhaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PEACOCK_BASS, PeacockBassEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.NEON_TETRA, NeonTetraEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.BETTA_FISH, BettaFishEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ARAPAIMA, ArapaimaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CHEETAH, CheetahEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANGLERFISH, AnglerfishEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.FRESHWATER_STINGRAY, FreshwaterStingrayEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HERMIT_CRAB, HermitCrabEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.RACCOON, RaccoonEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CAPYBARA, CapybaraEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.HYENA, HyenaEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANCIENT_SKELETON, AncientSkeletonEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.UNICORN, UnicornEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CATFISH, CatfishEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.RAT, RatEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PENGUIN, PenguinEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CARP, CarpEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.CAVE_WYVERN, CaveWyvernEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TERMITE, TermiteEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANT, AntEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.ANTEATER, AnteaterEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.KOMODO_DRAGON, KomodoDragonEntity.setAttributes());

		FabricDefaultAttributeRegistry.register(ModEntities.CROW, CrowEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.PIGEON, PigeonEntity.setAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.OWL, OwlEntity.setAttributes());

		ModScreenHandlers.registerScreenHandlers();
		ModEvents.registerModEvents();

		ModTrades.registerCustomTrades();

		ModLootTableModifiers.modifyLootTables();

		ModEntities.registerModEntities();

		DispenserBlock.registerBehavior(ModItems.QUILL, new QuillDispenserBehavior(ModItems.QUILL));

		ModBlockEntities.registerBlockEntities();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();

		ModParticles.registerParticles();
		ModSounds.registerSounds();

		ModEntityGeneration.addSpawns();
		ModWorldGeneration.generateModWorldGen();

		ModEffects.registerEffects();
		ModPotions.registerPotions();

		ModDataComponents.registerDataComponents();

		StrippableBlockRegistry.register(ModBlocks.SPECTRAL_LOG, ModBlocks.STRIPPED_SPECTRAL_LOG);
		StrippableBlockRegistry.register(ModBlocks.SPECTRAL_WOOD, ModBlocks.STRIPPED_SPECTRAL_WOOD);

		StrippableBlockRegistry.register(ModBlocks.PALM_LOG, ModBlocks.STRIPPED_PALM_LOG);
		StrippableBlockRegistry.register(ModBlocks.PALM_WOOD, ModBlocks.STRIPPED_PALM_WOOD);

		StrippableBlockRegistry.register(ModBlocks.HOLLOW_ACACIA_LOG, ModBlocks.STRIPPED_HOLLOW_ACACIA_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_OAK_LOG, ModBlocks.STRIPPED_HOLLOW_OAK_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_BIRCH_LOG, ModBlocks.STRIPPED_HOLLOW_BIRCH_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_SPRUCE_LOG, ModBlocks.STRIPPED_HOLLOW_SPRUCE_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_DARK_OAK_LOG, ModBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_JUNGLE_LOG, ModBlocks.STRIPPED_HOLLOW_JUNGLE_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_MANGROVE_LOG, ModBlocks.STRIPPED_HOLLOW_MANGROVE_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_CHERRY_LOG, ModBlocks.STRIPPED_HOLLOW_CHERRY_LOG);

		StrippableBlockRegistry.register(ModBlocks.HOLLOW_PALM_LOG, ModBlocks.STRIPPED_HOLLOW_PALM_LOG);
		StrippableBlockRegistry.register(ModBlocks.HOLLOW_SPECTRAL_LOG, ModBlocks.STRIPPED_HOLLOW_SPECTRAL_LOG);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_SPECTRAL_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_SPECTRAL_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_LEAVES, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_FENCE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_FENCE_GATE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_SLAB, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.SPECTRAL_STAIRS, 5, 20);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_PALM_LOG, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_PALM_WOOD, 5, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_PLANKS, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_LEAVES, 30, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_FENCE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_FENCE_GATE, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_SLAB, 5, 20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PALM_STAIRS, 5, 20);

		HOLLOW_LOGS.values().forEach(block ->
				FlammableBlockRegistry.getDefaultInstance().add(block, 5, 10)
		);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.WHITE_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.LIGHT_GRAY_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GRAY_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.BLACK_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.BLUE_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.CYAN_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.LIGHT_BLUE_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.GREEN_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.LIME_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PURPLE_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.MAGENTA_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PINK_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.YELLOW_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.ORANGE_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.RED_NET_BLOCK, 40, 60);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.BROWN_NET_BLOCK, 40, 60);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.FISH_TRAP_BLOCK, 10, 5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.PILE_OF_STICKS_BLOCK, 30, 60);

		FuelRegistry.INSTANCE.add(ModBlocks.PILE_OF_STICKS_BLOCK, 150);
		FuelRegistry.INSTANCE.add(ModItems.BARK, 100);
		FuelRegistry.INSTANCE.add(ModItems.SAWDUST, 30);
		FuelRegistry.INSTANCE.add(ModBlocks.SAWDUST_BLOCK, 120);

		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_SAPLING, 100);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_DOOR, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_TRAPDOOR, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_BUTTON, 100);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_PRESSURE_PLATE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_FENCE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_FENCE_GATE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_STAIRS, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.SPECTRAL_SLAB, 150);

		FuelRegistry.INSTANCE.add(ModBlocks.PALM_SAPLING, 100);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_DOOR, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_TRAPDOOR, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_BUTTON, 100);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_PRESSURE_PLATE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_FENCE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_FENCE_GATE, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_STAIRS, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.PALM_SLAB, 150);

		FuelRegistry.INSTANCE.add(ModBlocks.FISH_TRAP_BLOCK, 300);

		ModRecipes.registerRecipes();

		ModCompostables.register();

		ModTrunkPlacerTypes.register();
		ModFoliagePlacerTypes.register();

		ModFeatures.registerFeatures();

		ModStructures.registerStructureFeatures();

		RatSpawner ratSpawner = new RatSpawner();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			ratSpawner.spawn(world, world.getDifficulty() != Difficulty.PEACEFUL, world.getServer().shouldSpawnAnimals());
		});

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, ModItems.SPECTRAL_BAT_EAR, ModPotions.ECHOLOCATION_POTION);
		});

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, ModItems.RAW_EEL_MEAT, ModPotions.ELECTRIFYING_POTION);
		});
	}
}
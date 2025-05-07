package net.domixcze.domixscreatures;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.entity.ModBlockEntities;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.*;
import net.domixcze.domixscreatures.item.ModItemGroups;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.potion.ModPotions;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.structures.ModStructures;
import net.domixcze.domixscreatures.util.ModLootTableModifiers;
import net.domixcze.domixscreatures.util.QuillDispenserBehavior;
import net.domixcze.domixscreatures.world.gen.ModEntityGeneration;
import net.domixcze.domixscreatures.util.ModTrades;
import net.domixcze.domixscreatures.world.tree.ModFoliagePlacerTypes;
import net.domixcze.domixscreatures.world.tree.ModTrunkPlacerTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.block.DispenserBlock;
import net.minecraft.potion.Potions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomiXsCreatures implements ModInitializer {
	public static final String MOD_ID = "domixs-creatures";
    public static final Logger LOGGER = LoggerFactory.getLogger(DomiXsCreatures.MOD_ID);

	@Override
	public void onInitialize() {
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

		FabricDefaultAttributeRegistry.register(ModEntities.TEST, TestEntity.setAttributes());

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

		ModEffects.registerEffects();
		ModPotions.registerPotions();

		StrippableBlockRegistry.register(ModBlocks.SPECTRAL_LOG, ModBlocks.STRIPPED_SPECTRAL_LOG);
		StrippableBlockRegistry.register(ModBlocks.SPECTRAL_WOOD, ModBlocks.STRIPPED_SPECTRAL_WOOD);

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

		ModTrunkPlacerTypes.register();
		ModFoliagePlacerTypes.register();

		ModStructures.registerStructureFeatures();

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, ModItems.SPECTRAL_BAT_EAR, ModPotions.ECHOLOCATION_POTION);
		});
	}
}
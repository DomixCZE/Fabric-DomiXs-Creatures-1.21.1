package net.domixcze.domixscreatures;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.*;
import net.domixcze.domixscreatures.item.ModItemGroups;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.potion.ModPotions;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModLootTableModifiers;
import net.domixcze.domixscreatures.world.gen.ModEntityGeneration;
import net.domixcze.domixscreatures.util.ModTrades;
import net.domixcze.domixscreatures.world.tree.ModFoliagePlacerTypes;
import net.domixcze.domixscreatures.world.tree.ModTrunkPlacerTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

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

		//FabricDefaultAttributeRegistry.register(ModEntities.TEST, TestEntity.setAttributes());

		GeckoLib.initialize();

		ModTrades.registerCustomTrades();

		ModLootTableModifiers.modifyLootTables();

		ModEntities.registerModEntities();

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

		ModTrunkPlacerTypes.register();
		ModFoliagePlacerTypes.register();
	}
}
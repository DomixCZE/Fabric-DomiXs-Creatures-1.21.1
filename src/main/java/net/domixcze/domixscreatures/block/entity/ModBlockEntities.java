package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MolehillBlockEntity> MOLEHILL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "molehill_block_entity"),
                    BlockEntityType.Builder.create(MolehillBlockEntity::new,
                            ModBlocks.MOLEHILL_BLOCK).build());

    public static final BlockEntityType<PositiveMagnetBlockEntity> POSITIVE_MAGNET_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "positive_magnet_block_entity"),
                    BlockEntityType.Builder.create(PositiveMagnetBlockEntity::new,
                                    ModBlocks.POSITIVE_MAGNET_BLOCK).build());
    public static final BlockEntityType<NegativeMagnetBlockEntity> NEGATIVE_MAGNET_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "negative_magnet_block_entity"),
                    BlockEntityType.Builder.create(NegativeMagnetBlockEntity::new,
                                    ModBlocks.NEGATIVE_MAGNET_BLOCK).build());

    public static final BlockEntityType<FishTrapBlockEntity> FISH_TRAP_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "fish_trap_block_entity"),
                    BlockEntityType.Builder.create(FishTrapBlockEntity::new,
                            ModBlocks.FISH_TRAP_BLOCK).build());

    public static final BlockEntityType<AncientChestBlockEntity> ANCIENT_CHEST_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "ancient_chest_block_entity"),
                    BlockEntityType.Builder.create(AncientChestBlockEntity::new,
                            ModBlocks.ANCIENT_CHEST_BLOCK).build());

    public static final BlockEntityType<CursedAncientChestBlockEntity> CURSED_ANCIENT_CHEST_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "cursed_ancient_chest_block_entity"),
                    BlockEntityType.Builder.create(CursedAncientChestBlockEntity::new,
                            ModBlocks.CURSED_ANCIENT_CHEST_BLOCK).build());


    public static void registerBlockEntities() {
        DomiXsCreatures.LOGGER.info("Registering Block Entities for " + DomiXsCreatures.MOD_ID);
    }
}

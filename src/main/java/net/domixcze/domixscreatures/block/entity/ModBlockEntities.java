package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<MolehillBlockEntity> MOLEHILL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "molehill_block_entity"),
                    FabricBlockEntityTypeBuilder.create(MolehillBlockEntity::new,
                            ModBlocks.MOLEHILL_BLOCK).build());

    public static void registerBlockEntities() {
        DomiXsCreatures.LOGGER.info("Registering Block Entities for " + DomiXsCreatures.MOD_ID);
    }
}

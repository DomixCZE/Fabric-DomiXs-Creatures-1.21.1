package net.domixcze.domixscreatures.entity;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FireSalamanderEntity> FIRE_SALAMANDER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "fire_salamander"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, FireSalamanderEntity::new)
                    .dimensions(EntityDimensions.fixed(1.3f, 0.5f)).build());

    public static final EntityType<SpectralBatEntity> SPECTRAL_BAT = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "spectral_bat"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SpectralBatEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.0f)).build());

    public static final EntityType<WhaleEntity> WHALE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "whale"),
            FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, WhaleEntity::new)
                    .dimensions(EntityDimensions.fixed(3.0f, 1.8f)).build());

    public static final EntityType<GoldfishEntity> GOLDFISH = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "goldfish"),
            FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, GoldfishEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<WispEntity> WISP = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "wisp"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, WispEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<CrocodileEntity> CROCODILE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "crocodile"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CrocodileEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 0.7f)).build());

    public static final EntityType<BeaverEntity> BEAVER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "beaver"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BeaverEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.4f)).build());

    public static final EntityType<IguanaEntity> IGUANA = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "iguana"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, IguanaEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

    public static final EntityType<TigerEntity> TIGER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "tiger"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TigerEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build());

    public static final EntityType<DeerEntity> DEER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "deer"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DeerEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1.5f)).build());

    public static final EntityType<MooseEntity> MOOSE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "moose"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MooseEntity::new)
                    .dimensions(EntityDimensions.fixed(1.4f, 2.0f)).build());

    public static final EntityType<SharkEntity> SHARK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "shark"),
            FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, SharkEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 0.99f)).build());

    public static final EntityType<EelEntity> EEL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "eel"),
            FabricEntityTypeBuilder.create(SpawnGroup.WATER_CREATURE, EelEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.6f)).build());

    public static final EntityType<MudGolemEntity> MUD_GOLEM = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "mud_golem"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MudGolemEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.6f)).build());

    public static final EntityType<HippoEntity> HIPPO = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "hippo"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HippoEntity::new)
                    .dimensions(EntityDimensions.fixed(1.8f, 1.6f)).build());

    public static final EntityType<ShamanEntity> SHAMAN = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "shaman"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ShamanEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 2.0f)).build());

    public static final EntityType<VineEntity> VINE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "vine"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, VineEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1.8f)).build());

    public static final EntityType<MoleEntity> MOLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "mole"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MoleEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.3f)).build());

    public static final EntityType<WormEntity> WORM = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "worm"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WormEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f)).build());



    public static final EntityType<MagmaBallEntity> MAGMA_BALL = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "magma_ball"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, MagmaBallEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.8f)).build());



    /*public static final EntityType<TestEntity> TEST = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "test"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, TestEntity::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 0.8f)).build());*/

    public static void registerModEntities() {
        DomiXsCreatures.LOGGER.info("Registering Entities for " + DomiXsCreatures.MOD_ID);
    }
}
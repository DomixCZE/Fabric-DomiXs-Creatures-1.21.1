package net.domixcze.domixscreatures.entity;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<FireSalamanderEntity> FIRE_SALAMANDER = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "fire_salamander"),
            EntityType.Builder.create(FireSalamanderEntity::new, SpawnGroup.MONSTER)
                    .dimensions(1.3f, 0.5f).build());

    public static final EntityType<SpectralBatEntity> SPECTRAL_BAT = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "spectral_bat"),
            EntityType.Builder.create(SpectralBatEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.0f).build());

    public static final EntityType<WhaleEntity> WHALE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "whale"),
            EntityType.Builder.create(WhaleEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(3.0f, 1.8f).eyeHeight(0.85F).build());

    public static final EntityType<GoldfishEntity> GOLDFISH = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "goldfish"),
            EntityType.Builder.create(GoldfishEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<WispEntity> WISP = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "wisp"),
            EntityType.Builder.create(WispEntity::new, SpawnGroup.AMBIENT)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<CrocodileEntity> CROCODILE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "crocodile"),
            EntityType.Builder.create(CrocodileEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.5f, 0.7f).build());

    public static final EntityType<BeaverEntity> BEAVER = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "beaver"),
            EntityType.Builder.create(BeaverEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 0.4f).eyeHeight(0.25F).build());

    public static final EntityType<IguanaEntity> IGUANA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "iguana"),
            EntityType.Builder.create(IguanaEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<TigerEntity> TIGER = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "tiger"),
            EntityType.Builder.create(TigerEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.0f, 1.0f).build());

    public static final EntityType<DeerEntity> DEER = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "deer"),
            EntityType.Builder.create(DeerEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.8f, 1.5f).build());

    public static final EntityType<MooseEntity> MOOSE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "moose"),
            EntityType.Builder.create(MooseEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.4f, 2.0f).build());

    public static final EntityType<SharkEntity> SHARK = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "shark"),
            EntityType.Builder.create(SharkEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(1.5f, 0.99f).eyeHeight(0.65F).build());

    public static final EntityType<EelEntity> EEL = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "eel"),
            EntityType.Builder.create(EelEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.8f, 0.6f).eyeHeight(0.35F).build());

    public static final EntityType<MudGolemEntity> MUD_GOLEM = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "mud_golem"),
            EntityType.Builder.create(MudGolemEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 0.6f).build());

    public static final EntityType<HippoEntity> HIPPO = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "hippo"),
            EntityType.Builder.create(HippoEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.8f, 1.6f).build());

    public static final EntityType<ShamanEntity> SHAMAN = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "shaman"),
            EntityType.Builder.create(ShamanEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 2.0f).build());

    public static final EntityType<VineEntity> VINE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "vine"),
            EntityType.Builder.create(VineEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.8f, 1.8f).eyeHeight(0.5F).build());

    public static final EntityType<MoleEntity> MOLE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "mole"),
            EntityType.Builder.create(MoleEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.4f, 0.3f).build());

    public static final EntityType<WormEntity> WORM = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "worm"),
            EntityType.Builder.create(WormEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.2f, 0.2f).build());

    public static final EntityType<PorcupineEntity> PORCUPINE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "porcupine"),
            EntityType.Builder.create(PorcupineEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<WaterStriderEntity> WATER_STRIDER = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "water_strider"),
            EntityType.Builder.create(WaterStriderEntity::new, SpawnGroup.CREATURE)
                    .dimensions(2.0f, 1.5f).build());

    public static final EntityType<BoarEntity> BOAR = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "boar"),
            EntityType.Builder.create(BoarEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.9f, 0.9f).build());

    public static final EntityType<BisonEntity> BISON = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "bison"),
            EntityType.Builder.create(BisonEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.6f, 2.0f).build());

    public static final EntityType<SunBearEntity> SUN_BEAR = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "sun_bear"),
            EntityType.Builder.create(SunBearEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.2f, 1.3f).build());

    public static final EntityType<ButterflyEntity> BUTTERFLY = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "butterfly"),
            EntityType.Builder.create(ButterflyEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.4f, 0.4f).build());

    public static final EntityType<CaterpillarEntity> CATERPILLAR = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "caterpillar"),
            EntityType.Builder.create(CaterpillarEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.3f, 0.3f).build());

    public static final EntityType<GorillaEntity> GORILLA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "gorilla"),
            EntityType.Builder.create(GorillaEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.2f, 1.5f).build());

    public static final EntityType<PiranhaEntity> PIRANHA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "piranha"),
            EntityType.Builder.create(PiranhaEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.4f, 0.3f).build());

    public static final EntityType<PeacockBassEntity> PEACOCK_BASS = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "peacock_bass"),
            EntityType.Builder.create(PeacockBassEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.5f, 0.4f).build());

    public static final EntityType<NeonTetraEntity> NEON_TETRA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "neon_tetra"),
            EntityType.Builder.create(NeonTetraEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.2f, 0.2f).build());

    public static final EntityType<BettaFishEntity> BETTA_FISH = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "betta_fish"),
            EntityType.Builder.create(BettaFishEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.3f, 0.3f).build());

    public static final EntityType<ArapaimaEntity> ARAPAIMA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "arapaima"),
            EntityType.Builder.create(ArapaimaEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.8f, 0.6f).eyeHeight(0.35F).build());

    public static final EntityType<AnglerfishEntity> ANGLERFISH = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "anglerfish"),
            EntityType.Builder.create(AnglerfishEntity::new, SpawnGroup.UNDERGROUND_WATER_CREATURE)
                    .dimensions(0.5f, 0.7f).build());

    public static final EntityType<FreshwaterStingrayEntity> FRESHWATER_STINGRAY = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "freshwater_stingray"),
            EntityType.Builder.create(FreshwaterStingrayEntity::new, SpawnGroup.WATER_CREATURE)
                    .dimensions(0.6f, 0.2f).build());

    public static final EntityType<CheetahEntity> CHEETAH = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "cheetah"),
            EntityType.Builder.create(CheetahEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.7f, 0.9f).build());

    public static final EntityType<HermitCrabEntity> HERMIT_CRAB = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "hermit_crab"),
            EntityType.Builder.create(HermitCrabEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.4f, 0.4f).build());

    public static final EntityType<RaccoonEntity> RACCOON = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "raccoon"),
            EntityType.Builder.create(RaccoonEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 0.5f).build());

    public static final EntityType<CapybaraEntity> CAPYBARA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "capybara"),
            EntityType.Builder.create(CapybaraEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.2f, 1.0f).build());

    public static final EntityType<HyenaEntity> HYENA = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "hyena"),
            EntityType.Builder.create(HyenaEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.7f, 0.9f).build());

    public static final EntityType<AncientSkeletonEntity> ANCIENT_SKELETON = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "ancient_skeleton"),
            EntityType.Builder.create(AncientSkeletonEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.6f, 2.0f).build());

    public static final EntityType<UnicornEntity> UNICORN = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "unicorn"),
            EntityType.Builder.create(UnicornEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.4f, 1.6f).build());

    public static final EntityType<CatfishEntity> CATFISH = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "catfish"),
            EntityType.Builder.create(CatfishEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.0f, 0.5f).build());




    public static final EntityType<MagmaBallEntity> MAGMA_BALL = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "magma_ball"),
            EntityType.Builder.create(MagmaBallEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8f, 0.8f).build());

    public static final EntityType<QuillProjectileEntity> QUILL_PROJECTILE = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "quill_projectile"),
            EntityType.Builder.<QuillProjectileEntity>create(QuillProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f).build());


    public static final EntityType<TestEntity> TEST = Registry.register(
            Registries.ENTITY_TYPE, Identifier.of(DomiXsCreatures.MOD_ID, "test"),
            EntityType.Builder.create(TestEntity::new, SpawnGroup.CREATURE)
                    .dimensions(1.2f, 1.3f).build());

    public static void registerModEntities() {
        DomiXsCreatures.LOGGER.info("Registering Entities for " + DomiXsCreatures.MOD_ID);
    }
}
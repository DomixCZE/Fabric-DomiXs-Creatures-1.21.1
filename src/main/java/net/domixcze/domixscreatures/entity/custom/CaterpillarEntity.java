package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.caterpillar.CaterpillarVariants;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CaterpillarEntity extends AnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(CaterpillarEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_COCOON = DataTracker.registerData(CaterpillarEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int preCocoonTimer = 0;
    private int cocoonTicks = 0;

    public CaterpillarEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new CaterpillarWanderGoal(this, 1.0));
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public void startCocooning() {
        this.setCocoon(true);
        this.cocoonTicks = 0;
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.isCocoon()) {
            preCocoonTimer++;
            if (preCocoonTimer >= 12000 && !this.getWorld().isClient && ModConfig.INSTANCE.enableCaterpillarTransformation) {
                this.startCocooning();
            }
        } else {
            cocoonTicks++;
            if (cocoonTicks >= 6000 && !this.getWorld().isClient && ModConfig.INSTANCE.enableCaterpillarTransformation) {
                this.transformIntoButterfly();
            }
        }
    }

    private void transformIntoButterfly() {
        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
            int amount = this.random.nextInt(2);
            this.dropStack(new ItemStack(net.minecraft.item.Items.STRING, amount));

            ButterflyEntity butterfly = new ButterflyEntity(ModEntities.BUTTERFLY, this.getWorld());
            butterfly.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

            // call the butterfly initialize method to get the variant behavior
            butterfly.initialize(
                    serverWorld,
                    serverWorld.getLocalDifficulty(butterfly.getBlockPos()),
                    SpawnReason.NATURAL,
                    null
            );

            this.getWorld().spawnEntity(butterfly);
            this.discard();
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_COCOON, false);
        builder.add(VARIANT, CaterpillarVariants.GREEN.getId());
    }

    public boolean isCocoon() {
        return this.dataTracker.get(IS_COCOON);
    }

    public void setCocoon(boolean cocoon) {
        this.dataTracker.set(IS_COCOON, cocoon);
    }

    public CaterpillarVariants getVariant() {
        return CaterpillarVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(CaterpillarVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public boolean isBaby() {
        return false; // caterpillar should not be a baby
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BREEDING) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.CATERPILLAR_SPAWNS_IN)) {
                this.setVariant(random.nextBoolean() ? CaterpillarVariants.BROWN : CaterpillarVariants.GREEN);
            } else {
                this.setVariant(CaterpillarVariants.GREEN);
            }
        }

        return entityData;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsCocoon", this.isCocoon());
        nbt.putInt("CocoonTicks", this.cocoonTicks);
        nbt.putInt("PreCocoonTimer", this.preCocoonTimer);
        nbt.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setCocoon(nbt.getBoolean("IsCocoon"));
        this.cocoonTicks = nbt.getInt("CocoonTicks");
        this.preCocoonTimer = nbt.getInt("PreCocoonTimer");
        this.setVariant(CaterpillarVariants.byId(nbt.getInt("Variant")));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.caterpillar.walk", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.caterpillar.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        return null;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    public static class CaterpillarWanderGoal extends WanderAroundFarGoal {
        private final CaterpillarEntity caterpillar;

        public CaterpillarWanderGoal(CaterpillarEntity caterpillar, double speed) {
            super(caterpillar, speed);
            this.caterpillar = caterpillar;
        }

        @Override
        public boolean canStart() {
            return !caterpillar.isCocoon() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !caterpillar.isCocoon() && super.shouldContinue();
        }
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.Beachable;
import net.domixcze.domixscreatures.entity.ai.BeachedGoal;
import net.domixcze.domixscreatures.entity.client.freshwater_stingray.FreshwaterStingrayVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FreshwaterStingrayEntity extends WaterCreatureEntity implements GeoEntity, Bucketable, Beachable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(FreshwaterStingrayEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FROM_BUCKET = DataTracker.registerData(FreshwaterStingrayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> BEACHED = DataTracker.registerData(FreshwaterStingrayEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final Map<UUID, Integer> stingCooldowns = new HashMap<>();

    public FreshwaterStingrayEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new BeachedGoal(this, this));
        this.goalSelector.add(1, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BUCKET) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.FRESHWATER_STINGRAY_SPAWNS_IN)) {
                // Assign a random variant
                FreshwaterStingrayVariants[] variants = FreshwaterStingrayVariants.values();
                FreshwaterStingrayVariants variant = variants[this.random.nextInt(variants.length)];
                this.setVariant(variant);
            } else {
                this.setVariant(FreshwaterStingrayVariants.YELLOW);
            }
        }

        return entityData;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, FreshwaterStingrayVariants.YELLOW.getId());
        builder.add(FROM_BUCKET, false);
        builder.add(BEACHED, false);
    }

    public boolean isBeached() {
        return this.dataTracker.get(BEACHED);
    }

    public void setBeached(boolean beached) {
        this.dataTracker.set(BEACHED, beached);
    }

    public boolean cannotDespawn() {
        return super.cannotDespawn() || this.isFromBucket();
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.isFromBucket() && !this.hasCustomName();
    }

    public FreshwaterStingrayVariants getVariant() {
        return FreshwaterStingrayVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(FreshwaterStingrayVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public boolean isFromBucket() {
        return this.dataTracker.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean fromBucket) {
        this.dataTracker.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbtCompound) ->
                nbtCompound.putInt("Variant", this.getVariant().getId()));
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        if (nbt.contains("Variant", NbtCompound.INT_TYPE)) {
            this.setVariant(FreshwaterStingrayVariants.byId(nbt.getInt("Variant")));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("FromBucket", this.isFromBucket());
        nbt.putBoolean("Beached", this.isBeached());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(FreshwaterStingrayVariants.byId(nbt.getInt("Variant")));
        this.setFromBucket(nbt.getBoolean("FromBucket"));
        this.setBeached(nbt.getBoolean("Beached"));
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(ModItems.FRESHWATER_STINGRAY_BUCKET);
    }

    public SoundEvent getBucketFillSound() {
        return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            stingCooldowns.replaceAll((uuid, ticks) -> ticks > 0 ? ticks - 1 : 0);

            List<LivingEntity> entities = this.getWorld().getEntitiesByClass(
                    LivingEntity.class,
                    this.getBoundingBox().expand(0.1),
                    e -> e != this
                            && e.isOnGround()
                            && EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(e)
                            && !e.getType().isIn(ModTags.EntityTypes.NOT_SCARY_FOR_FRESHWATER_STINGRAY)
            );

            for (LivingEntity entity : entities) {
                UUID id = entity.getUuid();
                if (stingCooldowns.getOrDefault(id, 0) == 0) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200)); // 10s
                    stingCooldowns.put(id, 100);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.freshwater_stingray.swim", Animation.LoopType.LOOP));
        } else if (isBeached()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.freshwater_stingray.beached", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.freshwater_stingray.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    //SOUNDS
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SALMON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SALMON_DEATH;
    }
}
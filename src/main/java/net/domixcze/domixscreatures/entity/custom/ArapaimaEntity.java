package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.client.arapaima.ArapaimaVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
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

public class ArapaimaEntity extends FishEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(ArapaimaEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int scaleDropCooldown = this.random.nextBetween(6000, 12000); // ~5–10 minutes in ticks

    public ArapaimaEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return FishEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));

        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BUCKET) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.ARAPAIMA_SPAWNS_IN)) {
                // Assign a random variant
                ArapaimaVariants[] variants = ArapaimaVariants.values();
                ArapaimaVariants variant = variants[this.random.nextInt(variants.length)];
                this.setVariant(variant);
            } else {
                this.setVariant(ArapaimaVariants.GREEN_RED);
            }
        }

        return entityData;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient && source.getSource() instanceof Entity projectile) {
            if (projectile.getType().isIn(ModTags.EntityTypes.REFLECTABLE_PROJECTILES)) {
                if (this.getRandom().nextFloat() < 0.8F) {
                    this.getWorld().playSound(
                            null,
                            this.getBlockPos(),
                            SoundEvents.ITEM_SHIELD_BLOCK,
                            SoundCategory.HOSTILE,
                            1.0F,
                            1.0F + (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F
                    );

                    return false; // cancel damage
                }
            }
        }

        return super.damage(source, amount);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, ArapaimaVariants.GREEN_RED.getId());
    }

    public ArapaimaVariants getVariant() {
        return ArapaimaVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(ArapaimaVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        super.copyDataToStack(stack);
        NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbtCompound) -> {
            nbtCompound.putInt("Variant", this.getVariant().getId());
            nbtCompound.putInt("ScaleDropCooldown", this.scaleDropCooldown);
        });
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        super.copyDataFromNbt(nbt);
        if (nbt.contains("Variant", NbtCompound.INT_TYPE)) {
            this.setVariant(ArapaimaVariants.byId(nbt.getInt("Variant")));
        }
        if (nbt.contains("ScaleDropCooldown", NbtCompound.INT_TYPE)) {
            this.scaleDropCooldown = nbt.getInt("ScaleDropCooldown");
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putInt("ScaleDropCooldown", this.scaleDropCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(ArapaimaVariants.byId(nbt.getInt("Variant")));
        scaleDropCooldown = nbt.getInt("ScaleDropCooldown");
    }

    @Override
    public ItemStack getBucketItem() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.arapaima.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.arapaima.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            scaleDropCooldown--;

            if (scaleDropCooldown <= 0) {
                dropScale();
                scaleDropCooldown = this.random.nextBetween(6000, 12000);
            }
        }
    }

    private void dropScale() {
        ItemStack stack;

        if (this.getVariant() == ArapaimaVariants.GREEN_RED) {
            stack = new ItemStack(ModItems.ARAPAIMA_SCALE_GREEN);
        } else {
            stack = new ItemStack(ModItems.ARAPAIMA_SCALE_BLACK);
        }

        this.dropStack(stack);
    }

    //SOUNDS
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_SALMON_FLOP;
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
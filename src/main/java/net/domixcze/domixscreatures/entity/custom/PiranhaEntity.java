package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ai.PiranhaMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.client.piranha.PiranhaVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.BleedingUtil;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
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

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class PiranhaEntity extends SchoolingFishEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(PiranhaEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public PiranhaEntity(EntityType<? extends SchoolingFishEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return SchoolingFishEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new PiranhaMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new PiranhaEatMeatGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, CodEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, SalmonEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, TropicalFishEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, BeaverEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, EelEntity.class, true));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BUCKET) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.PIRANHA_SPAWNS_IN)) {
                PiranhaVariants variant;

                if (entityData instanceof PiranhaData piranhaData) {
                    variant = piranhaData.variant;
                } else {
                    variant = random.nextBoolean()
                            ? PiranhaVariants.BLUE
                            : (random.nextBoolean() ? PiranhaVariants.GREEN : PiranhaVariants.BLACK);
                    entityData = new PiranhaData(this, variant);
                }

                this.setVariant(variant);
            } else {
                this.setVariant(PiranhaVariants.BLUE);
            }
        }

        return entityData;
    }

    //makes all the piranhas in one group have the same variant when they spawn. other variants can still join if there's space in the group
    private static class PiranhaData extends SchoolingFishEntity.FishData {
        final PiranhaVariants variant;

        PiranhaData(PiranhaEntity leader, PiranhaVariants variant) {
            super(leader);
            this.variant = variant;
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, PiranhaVariants.BLUE.getId());
    }

    public PiranhaVariants getVariant() {
        return PiranhaVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(PiranhaVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public void copyDataToStack(ItemStack stack) {
        super.copyDataToStack(stack);
        NbtComponent.set(DataComponentTypes.BUCKET_ENTITY_DATA, stack, (nbtCompound) ->
                nbtCompound.putInt("Variant", this.getVariant().getId()));
    }

    @Override
    public void copyDataFromNbt(NbtCompound nbt) {
        super.copyDataFromNbt(nbt);
        if (nbt.contains("Variant", NbtCompound.INT_TYPE)) {
            this.setVariant(PiranhaVariants.byId(nbt.getInt("Variant")));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(PiranhaVariants.byId(nbt.getInt("Variant")));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            int protectionCount = BleedingUtil.getBleedingProtectionCount(livingTarget);

            if (protectionCount == 0) {
                livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 250, 0));
            } else if (protectionCount <= 2) {
                livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 150, 0));
            } else if (protectionCount == 3) {
                livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 80, 0));
            }
        }
        return success;
    }

    @Override
    public ItemStack getBucketItem() {
        return new ItemStack(ModItems.PIRANHA_BUCKET);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<PiranhaEntity> Controller = new AnimationController<>(this, "controller", 3, this::predicate);
        Controller.triggerableAnim("attack", RawAnimation.begin().then("animation.piranha.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(Controller);
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.piranha.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.piranha.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
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

    public static class PiranhaEatMeatGoal extends Goal {
        private ItemEntity targetItem;
        private final PiranhaEntity piranha;

        private static final Predicate<ItemEntity> MEAT_ITEM_FILTER = (itemEntity) -> {
            ItemStack stack = itemEntity.getStack();
            return stack.isIn(ModTags.Items.RAW_MEAT);
        };

        public PiranhaEatMeatGoal(PiranhaEntity piranha) {
            this.piranha = piranha;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!piranha.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            }
            if (piranha.getTarget() != null || piranha.getAttacker() != null) {
                return false;
            }

            if (piranha.getRandom().nextInt(toGoalTicks(20)) != 0) {
                return false;
            }

            this.targetItem = findMeatItemInWater();

            return this.targetItem != null && piranha.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Nullable
        private ItemEntity findMeatItemInWater() {
            Box searchBox = piranha.getBoundingBox().expand(8.0, 8.0, 8.0);

            List<ItemEntity> items = piranha.getWorld().getEntitiesByClass(
                    ItemEntity.class,
                    searchBox,
                    (itemEntity) -> MEAT_ITEM_FILTER.test(itemEntity) && isItemInWater(itemEntity)
            );

            return items.isEmpty() ? null : items.getFirst();
        }

        private boolean isItemInWater(ItemEntity itemEntity) {
            return piranha.getWorld().getFluidState(itemEntity.getBlockPos()).isIn(FluidTags.WATER);
        }

        @Override
        public void start() {
            if (this.targetItem != null) {
                piranha.getNavigation().startMovingTo(this.targetItem, 1.2D);
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.targetItem != null
                    && this.targetItem.isAlive()
                    && piranha.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
                    && piranha.squaredDistanceTo(this.targetItem) < 256.0D;
        }

        @Override
        public void tick() {
            if (this.targetItem == null || !this.targetItem.isAlive()) {
                return;
            }

            piranha.getNavigation().startMovingTo(this.targetItem, 1.2D);

            if (piranha.squaredDistanceTo(this.targetItem) < 0.75D) {
                piranha.heal(2.0f);
                this.targetItem.getStack().decrement(1);
                if (this.targetItem.getStack().isEmpty()) {
                    this.targetItem.discard();
                }
                this.targetItem = null;
                piranha.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0f, 1.0f + (piranha.getRandom().nextFloat() - piranha.getRandom().nextFloat()) * 0.2f);
            }
        }

        @Override
        public void stop() {
            this.targetItem = null;
            piranha.getNavigation().stop();
        }
    }
}
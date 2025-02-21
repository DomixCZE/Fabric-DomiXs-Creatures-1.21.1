package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.deer.DeerAntlerSize;
import net.domixcze.domixscreatures.entity.client.deer.DeerVariants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeerEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANTLER_SIZE = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(DeerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public DeerEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, true, false, true, true, 7.0, 500, 700, true, false, true, true));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new FleeGoal<>(this, PlayerEntity.class, 10.0f, 1.0, 1.5));
        this.goalSelector.add(2, new FleeGoal<>(this, WolfEntity.class, 10.0f, 1.0, 1.5));
        this.goalSelector.add(2, new FleeGoal<>(this, PolarBearEntity.class, 10.0f, 1.0, 1.5));
        this.goalSelector.add(2, new FleeGoal<>(this, TigerEntity.class, 10.0f, 1.0, 1.5));
        this.goalSelector.add(3, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAS_SNOW_LAYER, false);
        this.dataTracker.startTracking(VARIANT, DeerVariants.BROWN.ordinal());
        this.dataTracker.startTracking(ANTLER_SIZE, DeerAntlerSize.NONE.ordinal());
        this.dataTracker.startTracking(SLEEPING, false);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
        if (sleeping) {
            this.getNavigation().stop();
        }
    }

    public DeerVariants getVariant() {
        return DeerVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(DeerVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    public DeerAntlerSize getAntlerSize() {
        return DeerAntlerSize.values()[this.dataTracker.get(ANTLER_SIZE)];
    }

    public void setAntlerSize(DeerAntlerSize size) {
        this.dataTracker.set(ANTLER_SIZE, size.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isSleeping()) {
            this.getNavigation().stop();
        }

        boolean isSnowing = this.getWorld().isRaining() && isInSnowyBiome();

        if (this.isInSnowyBiome() && isSnowing && !this.hasSnowLayer()) {
            snowTicks++;
            if (snowTicks >= 600) {
                this.setHasSnowLayer(true);
                snowTicks = 0;
            }
        }

        if ((this.isTouchingWater() || !this.isInSnowyBiome()) && this.hasSnowLayer()) {
            snowMeltTimer++;
            if (snowMeltTimer >= 200) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;
            }
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(DeerVariants.ALBINO);
        } else {
            this.setVariant(DeerVariants.BROWN);
        }

        if (!this.isBaby()) {
            if (world.getRandom().nextFloat() < 0.2f) {
                this.setAntlerSize(DeerAntlerSize.SMALL);
            } else if (world.getRandom().nextFloat() < 0.1f) {
                this.setAntlerSize(DeerAntlerSize.MEDIUM);
            } else if (world.getRandom().nextFloat() < 0.05f) {
                this.setAntlerSize(DeerAntlerSize.LARGE);
            } else {
                this.setAntlerSize(DeerAntlerSize.NONE);
            }
        }

        return entityData;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                }

                this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0F, 1.0F);

                if (!this.getWorld().isClient) {
                    int count = 3 + this.getWorld().random.nextInt(2);
                    this.dropStack(new ItemStack(Items.SNOWBALL, count));
                }

                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }


    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.6F, 1.2F);
        }
        return super.getDimensions(pose);
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();

        if (!this.getWorld().isClient()) {
            Random random = this.random;

            if (random.nextFloat() < 0.2f) {
                this.setAntlerSize(DeerAntlerSize.SMALL); //20%
            } else if (random.nextFloat() < 0.1f) {
                this.setAntlerSize(DeerAntlerSize.MEDIUM); //10%
            } else if (random.nextFloat() < 0.05f) {
                this.setAntlerSize(DeerAntlerSize.LARGE); //5%
            } else {
                this.setAntlerSize(DeerAntlerSize.NONE); //65%
            }
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        DeerEntity baby = (DeerEntity) ModEntities.DEER.create(world);
        if (baby != null) {
            DeerEntity parent1 = this;
            DeerEntity parent2 = (DeerEntity) entity;

            if ((parent1.getVariant() == DeerVariants.ALBINO) && (parent2.getVariant() == DeerVariants.ALBINO)) {
                baby.setVariant(DeerVariants.ALBINO);
            } else if (parent1.getVariant() == DeerVariants.ALBINO || parent2.getVariant() == DeerVariants.ALBINO) {
                double chance = 0.25;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(DeerVariants.ALBINO);
                } else {
                    baby.setVariant(DeerVariants.BROWN);
                }
            } else {
                double chance = 0.01;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(DeerVariants.ALBINO);
                } else {
                    baby.setVariant(DeerVariants.BROWN);
                }
            }
            baby.setAntlerSize(DeerAntlerSize.NONE);
        }
        return baby;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.WHEAT);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!(other instanceof DeerEntity)) {
            return false;
        } else {
            DeerEntity otherDeer = (DeerEntity) other;
            boolean thisHasAntlers = this.getAntlerSize() != DeerAntlerSize.NONE;
            boolean otherHasAntlers = otherDeer.getAntlerSize() != DeerAntlerSize.NONE;

            return this.isInLove() && other.isInLove() && (thisHasAntlers ^ otherHasAntlers);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putInt("AntlerSize", this.getAntlerSize().ordinal());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setVariant(DeerVariants.values()[nbt.getInt("Variant")]);
        this.setAntlerSize(DeerAntlerSize.values()[nbt.getInt("AntlerSize")]);
    }

    public boolean isInSnowyBiome() {
        BlockPos pos = this.getBlockPos();
        RegistryEntry<Biome> biomeEntry = this.getWorld().getBiome(pos);
        Biome biome = biomeEntry.value();
        return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
    }

    public boolean hasSnowLayer() {
        return this.dataTracker.get(HAS_SNOW_LAYER);
    }

    public void setHasSnowLayer(boolean hasSnow) {
        this.dataTracker.set(HAS_SNOW_LAYER, hasSnow);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_deer.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_deer.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.deer.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.deer.idle", Animation.LoopType.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_deer.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.deer.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final DeerEntity deer;

        public FleeGoal(DeerEntity deer, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(deer, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.deer = deer;
        }

        @Override
        public boolean canStart() {
            return !this.deer.isSleeping() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.deer.isSleeping() && super.shouldContinue();
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_HORSE_STEP, 0.15F, 1.0F);
    }
}
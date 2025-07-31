package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class SunBearEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(SunBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(SunBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_EATING = DataTracker.registerData(SunBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_HONEYED = DataTracker.registerData(SunBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.fixed(0.7F, 0.7F);
    private static final EntityDimensions ADULT_DIMENSIONS = EntityDimensions.fixed(1.2F, 1.3F);
    private static final EntityDimensions SLEEPING_BABY_DIMENSIONS = EntityDimensions.fixed(0.7F, 0.5F);
    private static final EntityDimensions SLEEPING_ADULT_DIMENSIONS = EntityDimensions.fixed(1.2F, 0.8F);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;
    private int eatingTimer = 0;
    private final int MAX_EATING_TIMER = 200;
    private int honeyTimer = 0;

    public SunBearEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 120, false, true, false, false, 5.0, 600, 800, false, false, true, true,2));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SunBearEatGoal(this));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new SunBearMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ProtectBabiesGoal<>(this, SunBearEntity.class, 8.0));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, DeerEntity.class, true));
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.HONEYCOMB ) && !this.isHoneyed() && !this.isBaby() && !this.isSleeping()) {
            this.setEating(true);
            this.eatingTimer = 0;
            this.setHoneyed(true);
            this.getNavigation().stop();
            if (!player.isCreative()) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        if (itemStack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemStack.damage(1, player, EquipmentSlot.MAINHAND);
                }

                this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0F, 1.0F);

                if (!this.getWorld().isClient) {
                    int count = 4 + this.getWorld().random.nextInt(3);
                    this.dropStack(new ItemStack(Items.SNOWBALL, count));
                }

                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    public boolean isBeingSnowedOn() {
        BlockPos blockPos = this.getBlockPos();
        return this.getWorld().isRaining() && this.isInSnowyBiome() && (this.hasSnow(blockPos) || this.hasSnow(BlockPos.ofFloored(blockPos.getX(), this.getBoundingBox().maxY, blockPos.getZ())));
    }

    public boolean hasSnow(BlockPos pos) {
        if (!this.getWorld().isRaining()) {
            return false;
        } else if (!this.getWorld().isSkyVisible(pos)) {
            return false;
        } else if (this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            Biome biome = this.getWorld().getBiome(pos).value();
            return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
        }
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

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
        if (sleeping) {
            this.getNavigation().stop();
        }
    }

    public boolean isEating() {
        return this.dataTracker.get(IS_EATING);
    }

    public void setEating(boolean eating) {
        this.dataTracker.set(IS_EATING, eating);
    }

    public boolean isHoneyed() {
        return this.dataTracker.get(IS_HONEYED);
    }

    public void setHoneyed(boolean honeyed) {
        this.dataTracker.set(IS_HONEYED, honeyed);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isEating()) {
            this.eatingTimer++;
            if (this.eatingTimer >= MAX_EATING_TIMER) {
                this.setEating(false);
                this.eatingTimer = 0;
                this.honeyTimer = 6000; // 5 minutes
            }
        }

        if (this.honeyTimer > 0) {
            this.honeyTimer--;
            if (this.honeyTimer == 0 && this.isHoneyed()) {
                this.setHoneyed(false);
            }
        }

        if (!this.hasSnowLayer() && this.isBeingSnowedOn()) {
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
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SLEEPING.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return this.getCustomDimensions(pose);
    }

    private EntityDimensions getCustomDimensions(EntityPose pose) {
        if (this.isSleeping()) {
            return this.isBaby() ? SLEEPING_BABY_DIMENSIONS : SLEEPING_ADULT_DIMENSIONS;
        }
        return this.isBaby() ? BABY_DIMENSIONS : ADULT_DIMENSIONS;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(SLEEPING, false);
        builder.add(IS_EATING, false);
        builder.add(IS_HONEYED, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("IsEating", this.isEating());
        nbt.putInt("EatingTimer", this.eatingTimer);
        nbt.putBoolean("IsHoneyed", this.isHoneyed());
        nbt.putInt("HoneyTimer", this.honeyTimer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setEating(nbt.getBoolean("IsEating"));
        this.eatingTimer = nbt.getInt("EatingTimer");
        this.setHoneyed(nbt.getBoolean("IsHoneyed"));
        this.honeyTimer = nbt.getInt("HoneyTimer");
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.SUN_BEAR_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.SUN_BEAR.create(world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_sun_bear.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_sun_bear.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isEating()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.sun_bear.eat", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.sun_bear.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.sun_bear.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_sun_bear.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.sun_bear.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_sun_bear.swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.sun_bear.swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    public static class SunBearEatGoal extends Goal {
        private final SunBearEntity sunBear;
        private int eatingSoundCooldown = 10;

        public SunBearEatGoal(SunBearEntity sunBear) {
            this.sunBear = sunBear;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.sunBear.isEating() && !this.sunBear.isSleeping() && !this.sunBear.isBaby();
        }

        @Override
        public void start() {
            this.sunBear.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.sunBear.setEating(false);
        }

        @Override
        public boolean shouldContinue() {
            return this.sunBear.isEating() && !this.sunBear.isSleeping();
        }

        @Override
        public void tick() {
            this.sunBear.getNavigation().stop();
            this.sunBear.getLookControl().tick();

            if (eatingSoundCooldown <= 0) {
                this.sunBear.getWorld().playSound(null, this.sunBear.getX(), this.sunBear.getY(), this.sunBear.getZ(), SoundEvents.ENTITY_CAT_EAT, this.sunBear.getSoundCategory(), 1.0F, 1.0F);
                eatingSoundCooldown = 10;
            } else {
                eatingSoundCooldown--;
            }
        }
    }
}
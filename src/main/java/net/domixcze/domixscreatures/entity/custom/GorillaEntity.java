package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.entity.*;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
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

public class GorillaEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(GorillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(GorillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_EATING = DataTracker.registerData(GorillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(GorillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_CHEST_BANGING = DataTracker.registerData(GorillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.fixed(0.5F, 0.5F);
    private static final EntityDimensions ADULT_DIMENSIONS = EntityDimensions.fixed(1.2F, 1.5F);
    private static final EntityDimensions SLEEPING_BABY_DIMENSIONS = EntityDimensions.fixed(0.5F, 0.5F);
    private static final EntityDimensions SLEEPING_ADULT_DIMENSIONS = EntityDimensions.fixed(1.2F, 1.0F);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;
    private int eatingTimer = 0;
    private int sittingTimer = 0;
    private int chestBangTimer = 0;
    private int rideParentCooldown = 0;
    private static final int MAX_RIDE_PARENT_COOLDOWN = 100;
    private static final int MAX_CHEST_BANG_TIMER = 60;
    private final int MAX_EATING_TIMER = 200;
    private final int MAX_SITTING_TIMER = 1000;

    private int currentSittingCooldown;
    private int currentChestBangCooldown;
    private static final int MIN_SITTING_COOLDOWN = 20 * 30; // 30 seconds
    private static final int MAX_SITTING_COOLDOWN_RANGE = 20 * 60; // Additional 60 seconds (so 30-90 seconds)
    private static final int MIN_CHEST_BANG_COOLDOWN = 20 * 120; // 2 minutes
    private static final int MAX_CHEST_BANG_COOLDOWN_RANGE = 20 * 180; // Additional 3 minutes (so 2-5 minutes)

    public GorillaEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
        this.currentSittingCooldown = getRandomSittingCooldown(this.random);
        this.currentChestBangCooldown = getRandomChestBangCooldown(this.random);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 120, false, true, true, false, 5.0, 600, 800, true, false, true, true, 2));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GorillaEatGoal(this));
        this.goalSelector.add(2, new GorillaSitGoal(this));
        this.goalSelector.add(3, new GorillaChestBangGoal(this));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new GorillaMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(6, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(7, new RideParentGoal(this));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(9, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    public boolean isPushable() {
        return false;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(ModItems.BANANA) && !this.isBaby() && !this.isSleeping() && !this.isEating() && !this.isChestBanging() && !this.hasPassengers()) {
            this.currentSittingCooldown = getRandomSittingCooldown(this.random);
            this.setSitting(true);
            this.setEating(true);
            this.eatingTimer = 0;
            this.sittingTimer =0;
            this.getNavigation().stop();
            if (!player.isCreative()) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        if (itemStack.isOf(Items.BRUSH) && this.hasSnowLayer()) {
            if (!this.getWorld().isClient) {
                this.setHasSnowLayer(false);
                this.snowMeltTimer = 0;

                int count = 3 + this.getWorld().random.nextInt(2);
                this.dropStack(new ItemStack(Items.SNOWBALL, count));
            }

            SnowLayerUtil.spawnSnowParticles(this);

            this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0F, 1.0F);
            if (!player.isCreative()) {
                itemStack.damage(1, player, EquipmentSlot.MAINHAND);
            }

            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean hasSnowLayer() {
        return this.dataTracker.get(HAS_SNOW_LAYER);
    }

    @Override
    public void setHasSnowLayer(boolean hasSnow) {
        this.dataTracker.set(HAS_SNOW_LAYER, hasSnow);
    }

    @Override
    public int getSnowTicks() {
        return this.snowTicks;
    }

    @Override
    public void setSnowTicks(int ticks) {
        this.snowTicks = ticks;
    }

    @Override
    public int getSnowMeltTimer() {
        return this.snowMeltTimer;
    }

    @Override
    public void setSnowMeltTimer(int timer) {
        this.snowMeltTimer = timer;
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

    public boolean isSitting() {
        return this.dataTracker.get(IS_SITTING);
    }

    public void setSitting(boolean sitting) {
        this.dataTracker.set(IS_SITTING, sitting);
        if (sitting) this.getNavigation().stop();
    }

    public boolean isChestBanging() {
        return this.dataTracker.get(IS_CHEST_BANGING);
    }

    public void setChestBanging(boolean banging) {
        this.dataTracker.set(IS_CHEST_BANGING, banging);
        if (banging) this.getNavigation().stop();
    }

    @Override
    public void tick() {
        super.tick();

        if (rideParentCooldown > 0) {
            rideParentCooldown--;
        }

        if (this.currentChestBangCooldown > 0) {
            this.currentChestBangCooldown--;
        }

        if (this.isChestBanging()) {
            this.chestBangTimer++;
            if (this.chestBangTimer >= MAX_CHEST_BANG_TIMER) {
                this.setChestBanging(false);
                this.chestBangTimer = 0;
                this.currentChestBangCooldown = getRandomChestBangCooldown(this.random);
            }
        }

        if (this.isEating()) {
            this.eatingTimer++;
            if (this.eatingTimer >= MAX_EATING_TIMER) {
                this.setEating(false);
                this.eatingTimer = 0;
            }
        }

        if (this.currentSittingCooldown > 0) {
            this.currentSittingCooldown--;
        }

        if (this.isSitting()) {
            this.sittingTimer++;
            if (this.sittingTimer >= MAX_SITTING_TIMER) {
                this.setSitting(false);
                this.sittingTimer = 0;
                this.currentSittingCooldown = getRandomSittingCooldown(this.random);
            }
        }

        SnowLayerUtil.handleSnowLayerTick(this, this);
    }

    private int getRandomSittingCooldown(Random random) {
        return MIN_SITTING_COOLDOWN + random.nextInt(MAX_SITTING_COOLDOWN_RANGE);
    }

    private int getRandomChestBangCooldown(Random random) {
        return MIN_CHEST_BANG_COOLDOWN + random.nextInt(MAX_CHEST_BANG_COOLDOWN_RANGE);
    }

    @Override
    public void tickRiding() {
        super.tickRiding();

        if (!this.isBaby()) {
            this.stopRiding();
            this.rideParentCooldown = MAX_RIDE_PARENT_COOLDOWN;
            return;
        }

        if (this.hurtTime > 0) {
            this.stopRiding();
            this.rideParentCooldown = MAX_RIDE_PARENT_COOLDOWN;
            return;
        }

        if (this.isSleeping()) {
            this.stopRiding();
            this.rideParentCooldown = MAX_RIDE_PARENT_COOLDOWN;
            return;
        }

        if (this.hasVehicle() && this.getVehicle() instanceof GorillaEntity parent) {
            if (parent.isSleeping()) {
                this.stopRiding();
                this.rideParentCooldown = MAX_RIDE_PARENT_COOLDOWN;
                return;
            }

            if (parent.isTouchingWater() || parent.hurtTime > 0) {
                this.stopRiding();
                this.rideParentCooldown = MAX_RIDE_PARENT_COOLDOWN;
                return;
            }

            float parentYaw = parent.getHeadYaw();
            this.setHeadYaw(parentYaw);
            this.setBodyYaw(parentYaw);
            this.setYaw(parentYaw);
        }
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        Vec3d base = super.getPassengerAttachmentPos(passenger, dimensions, scaleFactor);

        if (passenger instanceof GorillaEntity baby && baby.isBaby()) {
            // Y: up/down
            // Z: forward/back
            // X: left/right
            Vec3d offset = new Vec3d(0.0, -0.45, -0.3).multiply(scaleFactor)
                    .rotateY(-this.getYaw() * ((float) Math.PI / 180F));

            return base.add(offset);
        }

        return base;
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
        builder.add(IS_SITTING, false);
        builder.add(IS_CHEST_BANGING, false);
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
        nbt.putBoolean("IsSitting", this.isSitting());
        nbt.putInt("SittingTimer", this.sittingTimer);

        nbt.putInt("SittingCooldown", this.currentSittingCooldown);
        nbt.putInt("ChestBangTimer", this.chestBangTimer);
        nbt.putInt("ChestBangCooldown", this.currentChestBangCooldown);
        nbt.putBoolean("IsChestBanging", this.isChestBanging());
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
        this.setSitting(nbt.getBoolean("IsSitting"));
        this.sittingTimer = nbt.getInt("SittingTimer");

        this.currentSittingCooldown = nbt.getInt("SittingCooldown");
        this.chestBangTimer = nbt.getInt("ChestBangTimer");
        this.currentChestBangCooldown = nbt.getInt("ChestBangCooldown");
        this.setChestBanging(nbt.getBoolean("IsChestBanging"));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.GORILLA_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.GORILLA.create(world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<GorillaEntity> landController = new AnimationController<>(this, "land_controller", 5, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.gorilla.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "ride_controller", 5, this::ridePredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_gorilla.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_gorilla.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isEating()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.eat", Animation.LoopType.LOOP));
            } else if (this.isSitting()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.sit", Animation.LoopType.LOOP));
            } else if (this.isChestBanging()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.chest_bang", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState ridePredicate(AnimationState<T> state) {
        if (this.hasVehicle() && this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_gorilla.ride", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_gorilla.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.sleep", Animation.LoopType.LOOP));
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
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_gorilla.swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.gorilla.swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    public static class GorillaEatGoal extends Goal {
        private final GorillaEntity gorilla;
        private int eatingSoundCooldown = 10;

        public GorillaEatGoal(GorillaEntity gorilla) {
            this.gorilla = gorilla;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.gorilla.isEating()
                    && !gorilla.isSleeping()
                    && !gorilla.isBaby()
                    && !gorilla.hasPassengers();
        }

        @Override
        public void start() {
            gorilla.getNavigation().stop();
        }

        @Override
        public void stop() {
            gorilla.setEating(false);
        }

        @Override
        public boolean shouldContinue() {
            return gorilla.isEating() && !gorilla.isSleeping();
        }

        @Override
        public void tick() {
            gorilla.getNavigation().stop();
            gorilla.getLookControl().tick();

            if (eatingSoundCooldown <= 0) {
                gorilla.getWorld().playSound(null, gorilla.getX(), gorilla.getY(), gorilla.getZ(), SoundEvents.ENTITY_CAT_EAT, gorilla.getSoundCategory(), 1.0F, 1.0F);
                eatingSoundCooldown = 10;
            } else {
                eatingSoundCooldown--;
            }
        }
    }

    public static class GorillaSitGoal extends Goal {
        private final GorillaEntity gorilla;

        public GorillaSitGoal(GorillaEntity gorilla) {
            this.gorilla = gorilla;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return !gorilla.isBaby()
                    && !gorilla.isSleeping()
                    && !gorilla.hasPassengers()
                    && (gorilla.isSitting() || gorilla.isEating() || (gorilla.currentSittingCooldown <= 0 && gorilla.isOnGround()));
        }

        @Override
        public boolean shouldContinue() {
            return (gorilla.isSitting() || gorilla.isEating()) && !gorilla.isSleeping();
        }

        @Override
        public void start() {
            if (!gorilla.isSitting()) {
                gorilla.setSitting(true);
                gorilla.sittingTimer = 0;
            }
            gorilla.getNavigation().stop();
        }

        @Override
        public void stop() {
            if (gorilla.isSitting() && !gorilla.isEating()) {
                gorilla.setSitting(false);
                gorilla.currentSittingCooldown = gorilla.getRandomSittingCooldown(gorilla.random);
                gorilla.sittingTimer = 0;
            }
        }

        @Override
        public void tick() {
            gorilla.getNavigation().stop();
            gorilla.getLookControl().tick();

            if (gorilla.isSitting()) {
                gorilla.sittingTimer++;
                if (gorilla.sittingTimer >= gorilla.MAX_SITTING_TIMER && !gorilla.isEating()) {
                    gorilla.setSitting(false);
                    gorilla.currentSittingCooldown = gorilla.getRandomSittingCooldown(gorilla.random);
                    gorilla.sittingTimer = 0;
                }
            }
        }
    }

    public static class GorillaChestBangGoal extends Goal {
        private final GorillaEntity gorilla;

        public GorillaChestBangGoal(GorillaEntity gorilla) {
            this.gorilla = gorilla;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return !gorilla.isSleeping()
                    && !gorilla.isBaby()
                    && !gorilla.isEating()
                    && !gorilla.isSitting()
                    && !gorilla.hasPassengers()
                    && gorilla.currentChestBangCooldown <= 0
                    && !gorilla.isChestBanging();
        }

        @Override
        public boolean shouldContinue() {
            return gorilla.isChestBanging();
        }

        @Override
        public void start() {
            gorilla.setChestBanging(true);
            gorilla.chestBangTimer = 0;
            gorilla.getNavigation().stop();
        }

        @Override
        public void stop() {
            gorilla.setChestBanging(false);
            gorilla.currentChestBangCooldown = gorilla.getRandomChestBangCooldown(gorilla.random);
            gorilla.chestBangTimer = 0;
        }

        @Override
        public void tick() {
            gorilla.getNavigation().stop();
            gorilla.getLookControl().tick();
        }
    }

    public static class RideParentGoal extends Goal {
        private final GorillaEntity child;
        private GorillaEntity nearestParent;

        public RideParentGoal(GorillaEntity child) {
            this.child = child;
            this.setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            if (!child.isBaby() || child.hasVehicle()) return false;
            if (child.rideParentCooldown > 0) return false;

            Predicate<GorillaEntity> parentPredicate = g ->
                    !g.isBaby() &&
                            g != child &&
                            g.isAlive() &&
                            !g.hasPassengers() &&
                            !g.isSitting() &&
                            !g.isEating() &&
                            !g.isChestBanging() &&
                            !g.isSleeping();

            List<GorillaEntity> nearbyAdults = child.getWorld()
                    .getEntitiesByClass(GorillaEntity.class, child.getBoundingBox().expand(4.0, 2.0, 4.0), parentPredicate);

            for (GorillaEntity adult : nearbyAdults) {
                this.nearestParent = adult;
                return true; // Found a suitable parent
            }

            return false; // No suitable parent found
        }

        @Override
        public boolean shouldContinue() {
            return child.isBaby() && child.hasVehicle() && child.getVehicle() instanceof GorillaEntity;
        }

        @Override
        public void start() {
            if (nearestParent != null && !nearestParent.hasPassengers()) {
                double distanceSq = child.squaredDistanceTo(nearestParent);
                if (distanceSq <= 2.25) {
                    child.startRiding(nearestParent, true);
                }
            }
        }

        @Override
        public void stop() {
            this.nearestParent = null;
        }
    }
}
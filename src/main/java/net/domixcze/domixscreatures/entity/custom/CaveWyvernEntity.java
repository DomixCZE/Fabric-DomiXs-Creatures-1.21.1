package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.CaveWyvernSoundHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class CaveWyvernEntity extends AnimalEntity implements GeoEntity, Sleepy, Chainable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BREATHING_POISON = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PALE = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BREATH_COOLDOWN = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> SUN_EXPOSURE = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> CHAIN_OWNER = DataTracker.registerData(CaveWyvernEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private static final EntityDimensions ADULT_DIMENSIONS = EntityDimensions.fixed(2.0F, 2.0F);
    private static final EntityDimensions SLEEPING_ADULT_DIMENSIONS = EntityDimensions.fixed(2.0F, 0.8F);

    private ChainData chainData;

    public CaveWyvernEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 300.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,true, true, true, true, 5.0, 1200, 1500, false, false, false, true, 2));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WyvernAvoidSunlightGoal(this));
        this.goalSelector.add(2, new WyvernEscapeSunlightGoal(this, 1.4));
        this.goalSelector.add(2, new CaveWyvernPoisonBreathGoal(this));
        this.goalSelector.add(3, new CaveWyvernMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0));

        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new RevengeGoal(this));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity living) {
            living.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.POISON, 200, 0),
                    this
            );
        }

        return success;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SLEEPING, false);
        builder.add(BREATHING_POISON, false);
        builder.add(BREATH_COOLDOWN, 0);
        builder.add(SUN_EXPOSURE, 0);
        builder.add(IS_PALE, false);
        builder.add(CHAIN_OWNER, Optional.empty());
    }

    @Override
    public @Nullable ChainData getChainData() { return this.chainData; }

    @Override
    public void setChainData(@Nullable ChainData chainData) { this.chainData = chainData; }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("BreathCooldown", this.getPoisonBreathCooldown());
        nbt.putInt("SunExposure", this.getSunExposure());
        nbt.putBoolean("IsPale", this.isPale());

        this.writeChainDataToNbt(nbt, this.chainData);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setPoisonBreathCooldown(nbt.getInt("BreathCooldown"));
        this.setSunExposure(nbt.getInt("SunExposure"));
        this.setPale(nbt.getBoolean("IsPale"));

        this.chainData = this.readChainDataFromNbt(nbt);
    }

    @Override
    public @Nullable UUID getChainOwner() {
        return this.dataTracker.get(CHAIN_OWNER).orElse(null);
    }

    @Override
    public void setChainOwner(@Nullable UUID uuid) {
        this.dataTracker.set(CHAIN_OWNER, Optional.ofNullable(uuid));
    }

    public void setBreathingPoison(boolean breathing) {
        this.dataTracker.set(BREATHING_POISON, breathing);
    }

    public boolean isBreathingPoison() {
        return this.dataTracker.get(BREATHING_POISON);
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

    public boolean isPale() {
        return this.dataTracker.get(IS_PALE);
    }

    public void setPale(boolean pale) {
        this.dataTracker.set(IS_PALE, pale);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SLEEPING.equals(data)) {
            this.calculateDimensions();
        }
        if (BREATHING_POISON.equals(data)) {
            if (this.getWorld().isClient && this.isBreathingPoison()) {
                triggerBreathSound();
            }
        }
        super.onTrackedDataSet(data);
    }

    private void triggerBreathSound() {
       CaveWyvernSoundHelper.playBreathSound(this);
    }

    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return this.getCustomDimensions(pose);
    }

    private EntityDimensions getCustomDimensions(EntityPose pose) {
        if (this.isSleeping()) {
            return SLEEPING_ADULT_DIMENSIONS;
        }
        return ADULT_DIMENSIONS;
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        Entity holder = this.getChainHolder();

        if (holder != null) {
            if (holder instanceof ChainAnchorEntity) {
                return ActionResult.PASS;
            }

            if (player.getStackInHand(hand).isEmpty()) {
                if (!this.getWorld().isClient) {
                    boolean shouldDrop = (holder instanceof PlayerEntity) && !player.getAbilities().creativeMode;
                    this.detachChain(shouldDrop);
                }
                return ActionResult.success(this.getWorld().isClient);
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            Chainable.tickChain(this);
        }

        int currentCooldown = this.getPoisonBreathCooldown();
        if (currentCooldown > 0) {
            this.setPoisonBreathCooldown(currentCooldown - 1);
        }

        if (this.isBreathingPoison()) {
            this.bodyYaw = this.headYaw;
            this.prevBodyYaw = this.prevHeadYaw;
        }

        if (!this.getWorld().isClient) {
            if (!this.isPale()) {
                if (isExposedToSun()) {
                    this.setSunExposure(this.getSunExposure() + 1);
                    if (this.getSunExposure() > 40) spawnSunSmokeParticles();

                    if (this.getSunExposure() >= 300) {
                        this.setPale(true);
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.CAVE_WYVERN_CONVERT, SoundCategory.HOSTILE, 1.0F, 0.6F);
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, -1, 0, false, false));
                        this.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, -1, 1, false, false));
                    }
                }
            } else {
                if (isExposedToSun() && this.age % 20 == 0) {
                    this.damage(this.getDamageSources().onFire(), 2.0F);
                }
            }
        }
    }

    public boolean isExposedToSun() {
        return this.getWorld().isDay() && this.getWorld().isSkyVisible(this.getBlockPos());
    }

    private void spawnSunSmokeParticles() {
        if (this.age % 4 == 0 && this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    net.minecraft.particle.ParticleTypes.SMOKE,
                    this.getX(),
                    this.getY() + (this.getHeight() * 0.95),
                    this.getZ(),
                    1,
                    0.3, 0.1, 0.3,
                    0.01
            );
        }
    }

    public void setSunExposure(int ticks) {
        this.dataTracker.set(SUN_EXPOSURE, ticks);
    }

    public int getSunExposure() {
        return this.dataTracker.get(SUN_EXPOSURE);
    }

    public void setPoisonBreathCooldown(int ticks) {
        this.dataTracker.set(BREATH_COOLDOWN, ticks);
    }

    public int getPoisonBreathCooldown() {
        return this.dataTracker.get(BREATH_COOLDOWN);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<CaveWyvernEntity> landController = new AnimationController<>(this, "land_controller", 3, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.cave_wyvern.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
        controllers.add(new AnimationController<>(this, "water_controller", 3, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 3, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9 && !this.isBreathingPoison()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.walk", Animation.LoopType.LOOP));
        } else if (this.isBreathingPoison()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.breath_attack", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBreathingPoison()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.breath_attack_swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.cave_wyvern.sleep", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient && source.getSource() instanceof Entity projectile) {
            if (projectile.getType().isIn(ModTags.EntityTypes.REFLECTABLE_PROJECTILES)) {

                float reflectChance = this.isPale() ? 0.25F : 1.0F;

                if (this.getRandom().nextFloat() < reflectChance) {
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.ENTITY_ARROW_HIT,
                            SoundCategory.HOSTILE, 1.0F, 1.0F + (this.getRandom().nextFloat() * 0.2F));

                    return false;
                }
            }
        }
        return super.damage(source, amount);
    }

    public static class WyvernAvoidSunlightGoal extends Goal {
        private final CaveWyvernEntity wyvern;

        public WyvernAvoidSunlightGoal(CaveWyvernEntity wyvern) {
            this.wyvern = wyvern;
        }

        public boolean canStart() {
            return this.wyvern.getWorld().isDay() && this.wyvern.isPale() && NavigationConditions.hasMobNavigation(this.wyvern);
        }

        public void start() {
            ((MobNavigation)this.wyvern.getNavigation()).setAvoidSunlight(true);
        }

        public void stop() {
            if (NavigationConditions.hasMobNavigation(this.wyvern)) {
                ((MobNavigation)this.wyvern.getNavigation()).setAvoidSunlight(false);
            }
        }
    }

    public static class WyvernEscapeSunlightGoal extends Goal {
        private final CaveWyvernEntity wyvern;
        private double targetX;
        private double targetY;
        private double targetZ;
        private final double speed;

        public WyvernEscapeSunlightGoal(CaveWyvernEntity wyvern, double speed) {
            this.wyvern = wyvern;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!this.wyvern.isExposedToSun() || !this.wyvern.isPale()) return false;

            return this.findShade();
        }

        private boolean findShade() {
            Random random = this.wyvern.getRandom();
            BlockPos blockPos = this.wyvern.getBlockPos();

            for (int i = 0; i < 20; ++i) {
                BlockPos potentialPos = blockPos.add(random.nextInt(30) - 15, random.nextInt(8) - 4, random.nextInt(30) - 15);

                if (!this.wyvern.getWorld().isSkyVisible(potentialPos) && this.wyvern.getPathfindingFavor(potentialPos) >= 0.0f) {
                    this.targetX = potentialPos.getX();
                    this.targetY = potentialPos.getY();
                    this.targetZ = potentialPos.getZ();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return !this.wyvern.getNavigation().isIdle() && this.wyvern.getWorld().isSkyVisible(this.wyvern.getBlockPos());
        }

        @Override
        public void start() {
            this.wyvern.setTarget(null);
            this.wyvern.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }
    }
}
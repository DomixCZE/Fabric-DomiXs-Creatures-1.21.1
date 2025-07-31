package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
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
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CheetahEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(CheetahEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_RUNNING = DataTracker.registerData(CheetahEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(CheetahEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final double MOB_RUNNING_VELOCITY_THRESHOLD = 0.05 * 0.05;

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public CheetahEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 150,false, true, true, false, 5.0, 600, 800, false, false, true, true, 1));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new CheetahMeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(3, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new ProtectBabiesGoal<>(this, CheetahEntity.class, 8.0));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, DeerEntity.class, true));
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.CHEETAH_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.CHEETAH.create(world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(IS_RUNNING, false);
        builder.add(SLEEPING, false);
    }

    public boolean isRunning() {
        return this.dataTracker.get(IS_RUNNING);
    }

    public void setRunning(boolean running) {
        this.dataTracker.set(IS_RUNNING, running);
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

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsRunning", this.isRunning());
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setRunning(nbt.getBoolean("IsRunning"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
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

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.5F, 0.5F);
        }
        return this.getType().getDimensions();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 3, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_cheetah.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_cheetah.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isRunning()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.cheetah.run", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9){
                state.getController().setAnimation(RawAnimation.begin().then("animation.cheetah.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.cheetah.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_cheetah.swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.cheetah.swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_cheetah.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.cheetah.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemStack.damage(1, player, EquipmentSlot.MAINHAND);
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
    public void tick() {
        super.tick();

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

        if (!this.getWorld().isClient()) {
            boolean ShouldBeRunning = false;

            if (this.isBaby()) {
                ShouldBeRunning = false;
            } else {
                if (!this.isSleeping()) {
                    LivingEntity target = this.getTarget();
                    if (target != null && target.isAlive()) {
                        boolean mobIsMovingFast = target.getVelocity().horizontalLengthSquared() > MOB_RUNNING_VELOCITY_THRESHOLD;

                        if (mobIsMovingFast) {
                            ShouldBeRunning = true;
                        }
                    }
                }
            }

            if (this.isRunning() != ShouldBeRunning) {
                this.setRunning(ShouldBeRunning);
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null && this.isOnGround()) {

            double desiredSpeed;
            if (this.isSleeping()) {
                desiredSpeed = 0.0D;
            } else {
                desiredSpeed = this.isRunning() ? 0.4f : 0.2f;
            }


            if (movementSpeed.getBaseValue() != desiredSpeed) {
                movementSpeed.setBaseValue(desiredSpeed);
            }
        }
    }
}
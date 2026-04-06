package net.domixcze.domixscreatures.entity.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.brain.entity.crow.CrowBrain;
import net.domixcze.domixscreatures.entity.ai.brain.util.TameableBird;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.ShoulderPetHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class CrowEntity extends TameableEntity implements GeoEntity, TameableBird {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final ImmutableList<SensorType<? extends Sensor<? super CrowEntity>>> SENSORS =
            ImmutableList.of(
                    SensorType.NEAREST_LIVING_ENTITIES
            );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES =
            ImmutableList.of(
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.PATH,
                    MemoryModuleType.VISIBLE_MOBS,
                    MemoryModuleType.BREED_TARGET,
                    MemoryModuleType.IS_PANICKING,
                    MemoryModuleType.ATTACK_TARGET
            );

    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(CrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> LANDING = DataTracker.registerData(CrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(CrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> CLEANING = DataTracker.registerData(CrowEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int featherCleaningTime = this.random.nextInt(12000) + 20000;

    private int cleanTicks = 0;
    private int flightTicks = 0;
    private int flightCooldown = 0;
    private boolean guarding;
    private int alertCheckTimer;
    private int alertSoundCooldown;

    private static final float CLEANING_FEATHER_DROP_CHANCE = 0.2F; // 20%
    private static final int DEFAULT_FLIGHT_COOLDOWN = 100;

    private static final int ALERT_CHECK_INTERVAL = 20;
    private static final int ALERT_SOUND_COOLDOWN_MIN = 60;
    private static final int ALERT_SOUND_COOLDOWN_RANDOM = 40;
    private static final double ALERT_HORIZONTAL_RANGE = 10.0D;
    private static final double ALERT_VERTICAL_RANGE = 4.0D;

    public CrowEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.switchToGroundMode();
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.15D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FLYING, false);
        builder.add(LANDING, false);
        builder.add(SITTING, false);
        builder.add(CLEANING, false);
    }

    @Override
    protected Brain.Profile<CrowEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return CrowBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Brain<CrowEntity> getBrain() {
        return (Brain<CrowEntity>) super.getBrain();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return super.getOwner();
    }

    @Override
    public boolean isTamed() {
        return super.isTamed();
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return this.createGroundNavigation(world);
    }

    private EntityNavigation createGroundNavigation(World world) {
        MobNavigation navigation = new MobNavigation(this, world);
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(true);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    private EntityNavigation createFlightNavigation(World world) {
        BirdNavigation navigation = new BirdNavigation(this, world);
        navigation.setCanPathThroughDoors(false);
        navigation.setCanSwim(true);
        navigation.setCanEnterOpenDoors(true);
        return navigation;
    }

    public void switchToGroundMode() {
        this.dataTracker.set(FLYING, false);
        this.dataTracker.set(LANDING, false);
        this.setNoGravity(false);
        this.moveControl = new MoveControl(this);
        this.navigation = this.createGroundNavigation(this.getWorld());
    }

    public void switchToFlightMode() {
        this.dataTracker.set(FLYING, true);
        this.dataTracker.set(LANDING, false);
        this.setNoGravity(true);
        this.moveControl = new FlightMoveControl(this, 20, true);
        this.navigation = this.createFlightNavigation(this.getWorld());
    }

    @Override
    public boolean isFlying() {
        return this.dataTracker.get(FLYING);
    }

    @Override
    public boolean isLanding() {
        return this.dataTracker.get(LANDING);
    }

    @Override
    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    @Override
    public void setSitting(boolean sitting) {
        super.setSitting(sitting);
        this.dataTracker.set(SITTING, sitting);
    }

    public boolean isGuarding() {
        return this.guarding;
    }

    public void setGuarding(boolean guarding) {
        this.guarding = guarding;
    }

    public boolean isCleaning() {
        return this.dataTracker.get(CLEANING);
    }

    public void setCleaning(boolean cleaning) {
        this.dataTracker.set(CLEANING, cleaning);
    }

    @Override
    public int getFlightTicks() {
        return this.flightTicks;
    }

    @Override
    public boolean canStartFlying() {
        return !this.isBaby()
                && !this.isFlying()
                && this.flightCooldown <= 0
                && !this.isSitting()
                && !this.hasVehicle()
                && !this.isCleaning();
    }

    @Override
    public void startFlying() {
        if (this.isFlying() || this.isBaby()) {
            return;
        }

        this.switchToFlightMode();
        this.flightTicks = 0;
        this.setVelocity(this.getVelocity().x, 0.42D, this.getVelocity().z);
        this.velocityDirty = true;
    }

    @Override
    public void startLanding() {
        if (!this.isFlying()) {
            return;
        }

        this.dataTracker.set(LANDING, true);
        this.setNoGravity(false);
    }

    @Override
    public void stopFlying() {
        if (!this.isFlying()) {
            return;
        }

        this.switchToGroundMode();
        this.flightTicks = 0;
        this.flightCooldown = DEFAULT_FLIGHT_COOLDOWN;
    }

    @Override
    public List<TagKey<EntityType<?>>> getNonScaryEntityTypeTags() {
        return List.of(
                ModTags.EntityTypes.NOT_SCARY_FOR_BIRD,
                ModTags.EntityTypes.NOT_SCARY_FOR_CROW
        );
    }

    @Override
    public boolean isScaredBy(LivingEntity entity) {
        if (!TameableBird.super.isScaredBy(entity)) {
            return false;
        }

        if (this.isTamed()) {
            UUID ownerUuid = this.getOwnerUuid();
            if (ownerUuid != null && ownerUuid.equals(entity.getUuid())) {
                return false;
            }
        }

        return !(entity instanceof PlayerEntity player) || !player.isSneaking();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.WHEAT_SEEDS) && !this.isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.setOwner(player);
                    this.setTarget(null);
                    this.setSitting(false);
                    this.setGuarding(false);
                    this.getNavigation().stop();
                    this.getWorld().sendEntityStatus(this, (byte) 7);
                } else {
                    this.getWorld().sendEntityStatus(this, (byte) 6);
                }

                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }

        if (!this.getWorld().isClient()
                && hand == Hand.MAIN_HAND
                && this.isTamed()
                && this.isOwner(player)
                && !this.isBreedingItem(itemStack)
                && itemStack.isOf(Items.STICK)
                && player.isSneaking()
                && !this.isBaby()) {

            this.setGuarding(!this.isGuarding());

            Text entityName = this.getDisplayName();
            Text action = Text.translatable(this.isGuarding()
                    ? "message.domixs-creatures.action.guarding"
                    : "message.domixs-creatures.action.not_guarding");

            Text message = Text.literal(entityName.getString() + " ")
                    .append(action)
                    .append(".")
                    .styled(style -> style.withColor(Formatting.GREEN));

            player.sendMessage(message, true);
            return ActionResult.SUCCESS;
        }

        if (!this.getWorld().isClient()
                && hand == Hand.MAIN_HAND
                && this.isTamed()
                && this.isOwner(player)
                && !this.isBreedingItem(itemStack)
                && itemStack.isOf(Items.STICK)) {

            this.setSitting(!this.isSitting());

            Text entityName = this.getDisplayName();
            Text action = Text.translatable(this.isSitting()
                    ? "message.domixs-creatures.action.sitting"
                    : "message.domixs-creatures.action.following");

            Text message = Text.literal(entityName.getString() + " ")
                    .append(action)
                    .append(".")
                    .styled(style -> style.withColor(Formatting.GREEN));

            player.sendMessage(message, true);
            return ActionResult.SUCCESS;
        }

        if (this.isTamed() && !this.isBaby() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
            if (!this.hasVehicle()) {
                if (ShoulderPetHelper.canAddCustomShoulderPet(player)) {
                    this.startRiding(player, true);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS;
                }
            }
        }

        return super.interactMob(player, hand);
    }

    private void tickGuardAlert() {
        if (this.alertSoundCooldown > 0) {
            this.alertSoundCooldown--;
        }

        if (this.alertCheckTimer > 0) {
            this.alertCheckTimer--;
            return;
        }

        this.alertCheckTimer = ALERT_CHECK_INTERVAL;

        if (this.isBaby() || !this.isTamed() || !this.isGuarding()) {
            return;
        }

        if (this.alertSoundCooldown > 0) {
            return;
        }

        Box searchBox = this.getBoundingBox().expand(ALERT_HORIZONTAL_RANGE, ALERT_VERTICAL_RANGE, ALERT_HORIZONTAL_RANGE);

        List<HostileEntity> hostiles = this.getWorld().getEntitiesByClass(
                HostileEntity.class,
                searchBox,
                hostile -> hostile.isAlive() && !hostile.isSpectator() && this.canSee(hostile)
        );

        if (!hostiles.isEmpty()) {
            this.playSound(ModSounds.CROW_ALERT, 3.0F, 1.0F + this.random.nextFloat() * 0.2F);
            this.alertSoundCooldown = ALERT_SOUND_COOLDOWN_MIN + this.random.nextInt(ALERT_SOUND_COOLDOWN_RANDOM + 1);
        }
    }

    @Override
    protected void mobTick() {
        if (!this.getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();

            if (this.isBaby() && this.isFlying()) {
                this.stopFlying();
            }

            this.tickGuardAlert();

            if (this.cleanTicks > 0) {
                this.cleanTicks--;

                if (this.isFlying()) {
                    this.stopFlying();
                }

                this.getNavigation().stop();
                this.getBrain().forget(MemoryModuleType.WALK_TARGET);
                this.getBrain().forget(MemoryModuleType.LOOK_TARGET);

                if (this.cleanTicks <= 0) {
                    this.setCleaning(false);
                    this.tryDropFeatherFromCleaning();
                }

                super.mobTick();
                return;
            }

            if (this.isSitting() || this.hasVehicle()) {
                if (this.isFlying()) {
                    this.stopFlying();
                }

                this.getNavigation().stop();
                this.getBrain().forget(MemoryModuleType.WALK_TARGET);
                this.getBrain().forget(MemoryModuleType.LOOK_TARGET);

                super.mobTick();
                return;
            }

            if (this.isFlying()) {
                this.flightTicks++;
            }

            if (this.flightCooldown > 0) {
                this.flightCooldown--;
            }

            if (this.isFlying() && this.isOnGround()) {
                this.stopFlying();
            }

            this.getWorld().getProfiler().push("crowBrain");
            this.getBrain().tick(serverWorld, this);
            this.getWorld().getProfiler().swap("crowActivityUpdate");
            CrowBrain.updateActivities(this);
            this.getWorld().getProfiler().pop();
        }

        super.mobTick();
    }

    private void tryDropFeatherFromCleaning() {
        if (this.random.nextFloat() < CLEANING_FEATHER_DROP_CHANCE) {
            this.dropItem(ModItems.CROW_FEATHER);
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.isBaby() && !this.isOnGround()) {
            Vec3d velocity = this.getVelocity();
            if (velocity.y < 0.0D) {
                this.setVelocity(velocity.x, velocity.y * 0.6D, velocity.z);
            }
        }

        if (!this.getWorld().isClient()
                && this.cleanTicks <= 0
                && --this.featherCleaningTime <= 0) {

            if (this.canStartCleaning()) {
                this.cleanTicks = 60;
                this.setCleaning(true);
                this.getNavigation().stop();
                //this.dropItem(ModItems.CROW_FEATHER);
            }

            this.featherCleaningTime = this.random.nextInt(9000) + 9000;
        }
    }

    private boolean canStartCleaning() {
        if (!this.isAlive() || this.isBaby() || this.hasVehicle() || this.isFlying()) {
            return false;
        }

        if (this.isSitting()) {
            return true;
        }

        return this.isOnGround() && this.getVelocity().horizontalLengthSquared() <= 1.0E-9;
    }

    @Override
    public void tickRiding() {
        super.tickRiding();

        if (this.hasVehicle()) {
            Entity vehicle = this.getVehicle();

            if (vehicle instanceof PlayerEntity player) {
                float bodyYaw = player.bodyYaw;
                this.setHeadYaw(bodyYaw);
                this.setBodyYaw(bodyYaw);

                if (player.isSubmergedInWater()) {
                    this.stopRiding();
                }

                if (!player.getShoulderEntityLeft().isEmpty()) {
                    this.stopRiding();
                }

                if (player.isSneaking() && player.isOnGround()) {
                    this.stopRiding();
                }

                if (player.isFallFlying()) {
                    this.stopRiding();
                }

                if (player.isCrawling()) {
                    this.stopRiding();
                }

                float yaw = (float) Math.toRadians(bodyYaw);
                float offsetX = 0.3975f;
                float offsetY = -0.2425f;
                float offsetZ = 0.0f;

                double newX = player.getX() + offsetX * Math.cos(yaw) - offsetZ * Math.sin(yaw);
                double newZ = player.getZ() + offsetZ * Math.cos(yaw) + offsetX * Math.sin(yaw);
                double newY = player.getY() + offsetY + player.getStandingEyeHeight();

                this.setPosition(newX, newY, newZ);
            }
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isFlying()) {
            if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
                float speed = this.isLanding() ? 0.16F : this.getMovementSpeed();
                this.updateVelocity(speed, movementInput);
                this.move(MovementType.SELF, this.getVelocity());

                if (this.isLanding()) {
                    this.setVelocity(this.getVelocity().multiply(0.91D, 0.98D, 0.91D));
                } else {
                    this.setVelocity(this.getVelocity().multiply(0.91D));
                }
            }
            return;
        }

        super.travel(movementInput);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == this.getDamageSources().inWall() && this.hasVehicle()) {
            return false;
        }

        if (ShoulderPetHelper.shouldIgnoreMountedOwnerDamage(this, source)) {
            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.CROW_FOR_BREEDING);
    }

    @Nullable
    @Override
    public CrowEntity createChild(ServerWorld world, PassiveEntity entity) {
        CrowEntity baby = ModEntities.CROW.create(world);
        if (baby != null && this.isTamed()) {
            baby.setOwnerUuid(this.getOwnerUuid());
            baby.setTamed(true, true);
        }
        return baby;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Flying", this.isFlying());
        nbt.putBoolean("Landing", this.isLanding());
        nbt.putBoolean("IsGuarding", this.isGuarding());
        nbt.putBoolean("IsSitting", this.isSitting());
        nbt.putInt("FlightTicks", this.flightTicks);
        nbt.putInt("FlightCooldown", this.flightCooldown);
        nbt.putInt("FeatherCleaningTime", this.featherCleaningTime);

        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        boolean flying = nbt.getBoolean("Flying");
        boolean landing = nbt.getBoolean("Landing");

        this.setGuarding(nbt.getBoolean("IsGuarding"));
        this.setSitting(nbt.getBoolean("IsSitting"));
        this.flightTicks = nbt.getInt("FlightTicks");
        this.flightCooldown = nbt.getInt("FlightCooldown");

        if (nbt.contains("FeatherCleaningTime")) {
            this.featherCleaningTime = nbt.getInt("FeatherCleaningTime");
        }

        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }

        if (flying && !this.isBaby()) {
            this.switchToFlightMode();
            if (landing) {
                this.startLanding();
            }
        } else {
            this.switchToGroundMode();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 5, this::mainPredicate));
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        if (this.hasVehicle()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.crow.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isBaby() && !this.isOnGround()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crow.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isFlying()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.crow.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isCleaning()) {
            if (this.isSitting()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crow.clean_sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crow.clean_idle", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }

        if (this.isSitting()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crow.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crow.sit", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }

        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crow.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crow.walk", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crow.idle", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crow.idle", Animation.LoopType.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isGuarding() ? null : ModSounds.CROW_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CROW_HURT;
    }
}

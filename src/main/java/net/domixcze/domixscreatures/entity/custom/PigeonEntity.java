package net.domixcze.domixscreatures.entity.custom;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.brain.entity.pigeon.PigeonBrain;
import net.domixcze.domixscreatures.entity.ai.brain.util.TameableBird;
import net.domixcze.domixscreatures.entity.client.pigeon.PigeonVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class PigeonEntity extends TameableEntity implements GeoEntity, TameableBird {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final ImmutableList<SensorType<? extends Sensor<? super PigeonEntity>>> SENSORS =
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

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FLYING = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> LANDING = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(PigeonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int flightTicks = 0;
    private int flightCooldown = 0;

    private static final int DEFAULT_FLIGHT_COOLDOWN = 200;

    public PigeonEntity(EntityType<? extends TameableEntity> entityType, World world) {
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
        builder.add(VARIANT, PigeonVariants.GRAY.getId());
        builder.add(FLYING, false);
        builder.add(LANDING, false);
        builder.add(SITTING, false);
    }

    @Override
    public int getRandomTakeoffInterval() {
        return 600;
    }

    @Override
    protected Brain.Profile<PigeonEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    @Override
    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return PigeonBrain.create(this.createBrainProfile().deserialize(dynamic));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Brain<PigeonEntity> getBrain() {
        return (Brain<PigeonEntity>) super.getBrain();
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

    @Override
    public int getFlightTicks() {
        return this.flightTicks;
    }

    @Override
    public boolean canStartFlying() {
        return !this.isBaby() && !this.isFlying() && this.flightCooldown <= 0 && !this.isSitting() && !this.hasVehicle();
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
                ModTags.EntityTypes.NOT_SCARY_FOR_PIGEON
        );
    }

    @Override
    public boolean isScaredBy(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return false;
        }

        return TameableBird.super.isScaredBy(entity);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(ModItems.WORM) && !this.isTamed()) {
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
                && !this.isBreedingItem(itemStack)) {

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

        return super.interactMob(player, hand);
    }


    @Override
    protected void mobTick() {
        if (!this.getWorld().isClient()) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();

            if (this.isBaby() && this.isFlying()) {
                this.stopFlying();
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

            this.getWorld().getProfiler().push("pigeonBrain");
            this.getBrain().tick(serverWorld, this);
            this.getWorld().getProfiler().swap("pigeonActivityUpdate");
            PigeonBrain.updateActivities(this);
            this.getWorld().getProfiler().pop();
        }

        super.mobTick();
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("Flying", this.isFlying());
        nbt.putBoolean("Landing", this.isLanding());
        nbt.putInt("FlightTicks", this.flightTicks);
        nbt.putInt("FlightCooldown", this.flightCooldown);
        nbt.putBoolean("IsSitting", this.isSitting());

        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(PigeonVariants.byId(nbt.getInt("Variant")));
        boolean flying = nbt.getBoolean("Flying");
        boolean landing = nbt.getBoolean("Landing");
        this.flightTicks = nbt.getInt("FlightTicks");
        this.flightCooldown = nbt.getInt("FlightCooldown");
        this.setSitting(nbt.getBoolean("IsSitting"));

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

    public PigeonVariants getVariant() {
        return PigeonVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(PigeonVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BREEDING) {

            // Assign a random variant
            PigeonVariants[] variants = PigeonVariants.values();
            PigeonVariants variant = variants[this.random.nextInt(variants.length)];
            this.setVariant(variant);
        }

        return entityData;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.PIGEON_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        PigeonEntity baby = ModEntities.PIGEON.create(world);
        if (baby != null) {
            PigeonEntity parent1 = this;
            PigeonEntity parent2 = (PigeonEntity) entity;

            PigeonVariants variant1 = parent1.getVariant();
            PigeonVariants variant2 = parent2.getVariant();

            if (variant1 == variant2) {
                baby.setVariant(variant1);
            } else {
                baby.setVariant(world.getRandom().nextBoolean() ? variant1 : variant2);
            }

            if (this.isTamed()) {
                baby.setOwnerUuid(this.getOwnerUuid());
                baby.setTamed(true, true);
            }
        }

        return baby;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 5, this::mainPredicate));
    }

    private <T extends GeoAnimatable> PlayState mainPredicate(AnimationState<T> state) {
        if (this.hasVehicle()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.pigeon.idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isBaby() && !this.isOnGround()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_pigeon.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isFlying()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.pigeon.fly", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isSitting()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_pigeon.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.pigeon.sit", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }

        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_pigeon.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.pigeon.walk", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_pigeon.idle", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.pigeon.idle", Animation.LoopType.LOOP));
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
        return ModSounds.PIGEON_AMBIENT;
    }
}

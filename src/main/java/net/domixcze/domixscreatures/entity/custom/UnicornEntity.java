package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.entity.client.unicorn.UnicornVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UnicornEntity extends AnimalEntity implements GeoEntity, Tameable, Sleepy, EatsGrass, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Boolean> IS_RUBBING = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_RUNNING = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> TAMED = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> TRUST = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_EATING = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(UnicornEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final double MOB_RUNNING_VELOCITY_THRESHOLD = 0.05 * 0.05;

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    private int soundTicks = 0;
    private long lastTrustFeedTime = 0;
    private int hornRubCooldown = 0;

    private static final EntityDimensions ADULT_DIMENSIONS = EntityDimensions.fixed(1.4F, 1.6F);
    private static final EntityDimensions SLEEPING_ADULT_DIMENSIONS = EntityDimensions.fixed(1.4F, 0.8F);

    public UnicornEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 120, true, false, true, false, 5.0, 600, 800, true, false, true, true, 1));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new RubHornOnLogGoal(this, 10.0));
        this.goalSelector.add(2, new FleeGoal(this, 10.0, 1.0, 2.0));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(4, new MobEatGrassGoal(this));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(TAMED, false);
        builder.add(TRUST, 0);
        builder.add(IS_RUNNING, false);
        builder.add(IS_RUBBING, false);
        builder.add(SLEEPING, false);
        builder.add(IS_EATING, false);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(VARIANT, UnicornVariants.PINK.getId());
        builder.add(OWNER_UUID, Optional.empty());
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

    public boolean isTamed() {
        return this.dataTracker.get(TAMED);
    }

    public void setTamed(boolean tamed) {
        this.dataTracker.set(TAMED, tamed);
    }

    public int getTrust() {
        return this.dataTracker.get(TRUST);
    }

    public void setTrust(int trust) {
        this.dataTracker.set(TRUST, MathHelper.clamp(trust, 0, 10));
    }

    public void updateTrust(int amount) {
        setTrust(getTrust() + amount); }

    public int getHornRubCooldown() {
        return hornRubCooldown;
    }

    public void resetHornRubCooldown() {
        hornRubCooldown = 600 + this.getRandom().nextInt(600);
    }

    public boolean isRunning() {
        return this.dataTracker.get(IS_RUNNING);
    }

    public void setRunning(boolean running) {
        this.dataTracker.set(IS_RUNNING, running);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isRubbing() {
        return this.dataTracker.get(IS_RUBBING);
    }

    public void setRubbing(boolean rubbing) {
        this.dataTracker.set(IS_RUBBING, rubbing);
    }

    public boolean isEating() {
        return this.dataTracker.get(IS_EATING);
    }

    public void setEating(boolean eating) {
        this.dataTracker.set(IS_EATING, eating);
    }

    @Override
    @Nullable
    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
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

    public UnicornVariants getVariant() {
        return UnicornVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(UnicornVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
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
            return SLEEPING_ADULT_DIMENSIONS;
        }
        return ADULT_DIMENSIONS;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Tamed", isTamed());
        nbt.putInt("Trust", getTrust());
        nbt.putInt("HornRubCooldown", this.hornRubCooldown);
        nbt.putLong("LastTrustFeedTime", lastTrustFeedTime);
        nbt.putBoolean("IsRunning", this.isRunning());
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);

        UUID owner = getOwnerUuid();
        if (owner != null) nbt.putUuid("Owner", owner);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setTamed(nbt.getBoolean("Tamed"));
        setTrust(nbt.getInt("Trust"));
        this.hornRubCooldown = nbt.getInt("HornRubCooldown");
        if (nbt.containsUuid("Owner")) setOwnerUuid(nbt.getUuid("Owner"));
        if (nbt.contains("LastTrustFeedTime")) this.lastTrustFeedTime = nbt.getLong("LastTrustFeedTime");
        this.setRunning(nbt.getBoolean("IsRunning"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setVariant(UnicornVariants.byId(nbt.getInt("Variant")));
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION) {

            UnicornVariants[] variants = UnicornVariants.values();
            UnicornVariants variant = variants[this.random.nextInt(variants.length)];
            this.setVariant(variant);
        }

        return entityData;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        long worldTime = this.getWorld().getTime();

        if (!this.isTamed() && itemStack.isOf(Items.GOLDEN_APPLE)) {
            this.setTamed(true);
            this.setOwnerUuid(player.getUuid());
            this.playSound(SoundEvents.ENTITY_HORSE_EAT, 1.0F, 1.0F);
            this.lastTrustFeedTime = this.getWorld().getTime() - 12000;
            if (!player.isCreative()) itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        if (this.isTamed() && this.getOwnerUuid() != null && player.getUuid().equals(this.getOwnerUuid()) && itemStack.isOf(Items.GOLDEN_CARROT) && this.getTrust() < 10) {

            if (worldTime - lastTrustFeedTime >= 12000) { // 10 minutes
                this.updateTrust(1);
                lastTrustFeedTime = worldTime;
                this.playSound(SoundEvents.ENTITY_HORSE_EAT, 1.0F, 1.0F);
                if (!player.isCreative()) itemStack.decrement(1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
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
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);

        if (!this.getWorld().isClient && this.isTamed() && this.getOwnerUuid() != null) {
            Entity attacker = source.getAttacker();
            if (attacker instanceof PlayerEntity player && player.getUuid().equals(this.getOwnerUuid())) {
                this.updateTrust(-1);
            }
        }

        return result;
    }

    @Override
    public void tick() {
        super.tick();

        SnowLayerUtil.handleSnowLayerTick(this, this);

        if (!this.getWorld().isClient()) {
            boolean currentlyMovingFast = this.getVelocity().horizontalLengthSquared() > MOB_RUNNING_VELOCITY_THRESHOLD;

            if (this.isRunning() != currentlyMovingFast) {
                this.setRunning(currentlyMovingFast);
            }
        }

        Vec3d velocity = this.getVelocity();
        if (this.getWorld().isClient() && velocity.horizontalLengthSquared() > MOB_RUNNING_VELOCITY_THRESHOLD) {
            if (this.age % 2 == 0) {
                double offsetX = (this.random.nextDouble() - 0.5) * 0.6;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.6;
                this.getWorld().addParticle(
                        ModParticles.UNICORN_DUST,
                        this.getX() + offsetX,
                        this.getY() + 0.2,
                        this.getZ() + offsetZ,
                        0, 0.01, 0
                );
            }
        }
        if (hornRubCooldown > 0) hornRubCooldown--;
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP; }
        {
            if (this.isRunning()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.run", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.walk", Animation.LoopType.LOOP));
            } else if (this.isRubbing()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.rubbing", Animation.LoopType.LOOP));
            } else if (this.isEating()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.eat", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.idle", Animation.LoopType.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP; }
        {
            state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.unicorn.sleep", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getFluidState().isIn(FluidTags.WATER) || state.getFluidState().isIn(FluidTags.LAVA)) return;

        if (this.isRunning()) {
            this.soundTicks++;
            if (this.soundTicks % 3 == 0) {
                if (this.random.nextInt(10) == 0) {
                    this.playSound(SoundEvents.ENTITY_HORSE_BREATHE);
                }
                this.playSound(SoundEvents.ENTITY_HORSE_GALLOP);
            }
        } else {
            this.playSound(SoundEvents.ENTITY_HORSE_STEP);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return SoundEvents.ENTITY_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_HORSE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HORSE_DEATH;
    }

    public static boolean canSpawn(EntityType<UnicornEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        if (!(world instanceof ServerWorldAccess serverAccess)) return false;
        ServerWorld w = serverAccess.toServerWorld();

        if (w.isDay()) return false;

        if (w.getMoonPhase() != 0) return false;

        if (w.isRaining() || w.isThundering()) return false;

        if (random.nextFloat() >= 0.5f) return false; // 50% chance

        return true;
    }

    public static class RubHornOnLogGoal extends Goal {
        private final UnicornEntity unicorn;
        private final double range;
        private BlockPos logPos;
        private int rubTime = 0;
        private static final int MAX_RUB_TIME = 100;

        public RubHornOnLogGoal(UnicornEntity unicorn, double range) {
            this.unicorn = unicorn;
            this.range = range;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (unicorn.isBaby() || unicorn.isInsideWaterOrBubbleColumn() || unicorn.getHornRubCooldown() > 0 || unicorn.isLeashed() || unicorn.isSleeping() || unicorn.getTrust() < 1)
                return false;

            logPos = findNearbyLogAtEyeHeight();
            return logPos != null;
        }

        @Override
        public boolean shouldContinue() {
            if (logPos == null) return false;
            if (!unicorn.getWorld().getBlockState(logPos).isIn(BlockTags.LOGS)) return false;
            if (unicorn.isLeashed()) return false;

            double dx = logPos.getX() + 0.5 - unicorn.getX();
            double dz = logPos.getZ() + 0.5 - unicorn.getZ();
            double horizontalDistSq = dx * dx + dz * dz;

            if (!unicorn.isRubbing()) {
                return horizontalDistSq > 0.25;
            } else {
                return rubTime < MAX_RUB_TIME && horizontalDistSq <= 4.0D;
            }
        }

        @Override
        public void start() {
            rubTime = 0;
            unicorn.setRubbing(false);

            if (logPos != null) {
                double dx = logPos.getX() + 0.5 - unicorn.getX();
                double dz = logPos.getZ() + 0.5 - unicorn.getZ();
                double horizontalDistSq = dx * dx + dz * dz;
                if (horizontalDistSq > 0.25) {
                    unicorn.getNavigation().startMovingTo(logPos.getX() + 0.5, unicorn.getY(), logPos.getZ() + 0.5, 1.0D);
                } else {
                    unicorn.setRubbing(true);
                }
            }
        }

        @Override
        public void tick() {
            if (logPos == null) return;

            double dx = logPos.getX() + 0.5 - unicorn.getX();
            double dz = logPos.getZ() + 0.5 - unicorn.getZ();
            float yaw = (float) (MathHelper.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
            unicorn.setYaw(yaw);
            unicorn.setHeadYaw(yaw);
            unicorn.bodyYaw = yaw;

            double horizontalDistSq = dx * dx + dz * dz;
            if (!unicorn.isRubbing()) {
                if (horizontalDistSq > 0.25) {
                    unicorn.getNavigation().startMovingTo(logPos.getX() + 0.5, unicorn.getY(), logPos.getZ() + 0.5, 1.0D);
                }

                if (horizontalDistSq <= 4.0D) {
                    unicorn.getNavigation().stop();
                    unicorn.setRubbing(true);
                    rubTime = 0;
                }
                return;
            }

            rubTime++;
            if (rubTime % 20 == 0) {
                unicorn.playSound(SoundEvents.BLOCK_WOOD_HIT, 0.6F, 0.9F + unicorn.getRandom().nextFloat() * 0.2F);
                if (unicorn.getWorld() instanceof ServerWorld serverWorld) {
                    Vec3d lookDir = unicorn.getRotationVec(1.0F);

                    double forwardOffset = 1.3;
                    double verticalOffset = 2.0;
                    double sideOffset = 0.0;

                    double px = unicorn.getX() + lookDir.x * forwardOffset;
                    double py = unicorn.getY() + verticalOffset;
                    double pz = unicorn.getZ() + lookDir.z * forwardOffset;

                    double spread = 0.05;

                    serverWorld.spawnParticles(
                            ModParticles.UNICORN_DUST,
                            px, py, pz,
                            4,
                            spread, spread, spread,
                            0.0
                    );
                }
            }

            if (rubTime >= MAX_RUB_TIME && !unicorn.getWorld().isClient()) {
                unicorn.resetHornRubCooldown();

                int trust = unicorn.getTrust();
                if (trust > 0) {
                    float dropChance = Math.min(trust * 0.2f, 1.0f); // +20% per trust level

                    if (unicorn.getRandom().nextFloat() < dropChance) {
                        ItemStack dust = new ItemStack(ModItems.UNICORN_DUST);

                        double spawnX = unicorn.getX();
                        double spawnY = unicorn.getBodyY(1.0);
                        double spawnZ = unicorn.getZ();

                        ItemEntity itemEntity = new ItemEntity(unicorn.getWorld(), spawnX, spawnY, spawnZ, dust);
                        itemEntity.setVelocity(
                                (unicorn.getRandom().nextDouble() - 0.5) * 0.1,
                                0.25 + unicorn.getRandom().nextDouble() * 0.1,
                                (unicorn.getRandom().nextDouble() - 0.5) * 0.1
                        );

                        unicorn.getWorld().spawnEntity(itemEntity);
                    }
                }
            }
        }

        @Override
        public void stop() {
            unicorn.getNavigation().stop();
            logPos = null;
            rubTime = 0;
            unicorn.setRubbing(false);
        }

        private BlockPos findNearbyLogAtEyeHeight() {
            World world = unicorn.getWorld();
            Random random = unicorn.getRandom();
            int rangeInt = (int) range;
            int eyeY = unicorn.getBlockY() + (int) unicorn.getStandingEyeHeight();

            for (int i = 0; i < 20; i++) {
                BlockPos candidate = unicorn.getBlockPos().add(
                        random.nextInt(rangeInt) - rangeInt / 2,
                        0,
                        random.nextInt(rangeInt) - rangeInt / 2
                ).withY(eyeY);

                if (world.getBlockState(candidate).isIn(BlockTags.LOGS)) {
                    return candidate;
                }
            }
            return null;
        }
    }

    private boolean playerHasTrustedUnicornNearby(PlayerEntity player) {
        List<UnicornEntity> unicorns = player.getWorld().getEntitiesByClass(
                UnicornEntity.class,
                player.getBoundingBox().expand(8.0),
                unicorn -> unicorn.isTamed()
                        && unicorn.getOwnerUuid() != null
                        && unicorn.getOwnerUuid().equals(player.getUuid())
                        && unicorn.getTrust() == 10
        );
        return !unicorns.isEmpty();
    }

    static class FleeGoal extends FleeEntityGoal<PlayerEntity> {
        private final UnicornEntity unicorn;

        public FleeGoal(UnicornEntity unicorn, double distance, double slowSpeed, double fastSpeed) {
            super(unicorn, PlayerEntity.class, (float) distance, slowSpeed, fastSpeed,
                    (targetEntity) -> {
                        if (!(targetEntity instanceof PlayerEntity player)) return false;

                        if (unicorn.playerHasTrustedUnicornNearby(player)) {
                            return false;
                        }

                        ItemStack stack = player.getMainHandStack();
                        return !player.isSneaking() || (!stack.isEmpty() && stack.isIn(ItemTags.WEAPON_ENCHANTABLE));
                    }
            );
            this.unicorn = unicorn;
        }

        @Override
        public boolean canStart() {
            if (unicorn.isTamed() || unicorn.isSleeping()) {
                return false;
            }

            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            if (unicorn.isTamed() || unicorn.isSleeping()) {
                return false;
            }

            return super.shouldContinue();
        }

        @Override
        public void start() {
            super.start();
            unicorn.setRunning(true);
        }

        @Override
        public void stop() {
            super.stop();
            unicorn.setRunning(false);
        }
    }
}
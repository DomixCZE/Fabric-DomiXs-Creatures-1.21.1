package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.util.ModTags;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

public class BisonEntity extends AnimalEntity implements GeoEntity, EatsGrass, Sleepy {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(BisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_CHARGING = DataTracker.registerData(BisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_PREPARING = DataTracker.registerData(BisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_EATING = DataTracker.registerData(BisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(BisonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    private int chargeCooldown;
    private final int DEFAULT_CHARGE_COOLDOWN = 2000;
    private int lookingTimer;
    private final int DEFAULT_LOOKING_TIME = 40;
    private int runningTimer;
    private final int DEFAULT_RUNNING_TIME = 20;
    private int proximityTimer;
    public Vec3d runDirection = Vec3d.ZERO;
    private float momentum = 0f;
    public final float CHARGE_SPEED = 0.8f;
    public final float MOMENTUM_DECREASE = 0.05f;

    public BisonEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 150,false, true, false, false, 5.0, 600, 800, false, false, true, true,3));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new ChargeGoal(this));
        this.goalSelector.add(3, new BisonMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new MobEatGrassGoal(this));
        this.goalSelector.add(5, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.75f));
        this.goalSelector.add(7, new LookAroundGoal(this));

        this.targetSelector.add(1, new ProtectBabiesGoal<>(this, BisonEntity.class, 8.0));
        this.targetSelector.add(2, new RevengeGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(IS_CHARGING, false);
        builder.add(IS_PREPARING, false);
        builder.add(IS_EATING, false);
        builder.add(SLEEPING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putInt("ProximityTimer", this.proximityTimer);
        nbt.putInt("ChargeCooldown", this.chargeCooldown);
        nbt.putInt("LookingTimer", this.lookingTimer);
        nbt.putInt("RunningTimer", this.runningTimer);
        nbt.putBoolean("IsCharging", this.isCharging());
        nbt.putBoolean("IsPreparing", this.isPreparing());
        nbt.putFloat("Momentum", this.momentum);
        nbt.putBoolean("Sleeping", this.isSleeping());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.proximityTimer = nbt.getInt("ProximityTimer");
        this.chargeCooldown = nbt.getInt("ChargeCooldown");
        this.lookingTimer = nbt.getInt("LookingTimer");
        this.runningTimer = nbt.getInt("RunningTimer");
        this.setCharging(nbt.getBoolean("IsCharging"));
        this.setPreparing(nbt.getBoolean("IsPreparing"));
        this.momentum = nbt.getFloat("Momentum");
        this.setSleeping(nbt.getBoolean("Sleeping"));
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

    @Override
    public void onEatGrass() {
        this.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.0F, 1.0F);
        if (this.isBaby()) {
            this.growUp(60);
        }
    }

    @Override
    public boolean canEatGrass() {
        return this.isBaby();
    }

    public boolean isEating() {
        return this.dataTracker.get(IS_EATING);
    }

    public void setEating(boolean eating) {
        this.dataTracker.set(IS_EATING, eating);
    }

    public boolean isCharging() {
        return this.dataTracker.get(IS_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.dataTracker.set(IS_CHARGING, charging);
    }

    public boolean isPreparing() {
        return this.dataTracker.get(IS_PREPARING);
    }

    public void setPreparing(boolean preparing) {
        this.dataTracker.set(IS_PREPARING, preparing);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_bison.walk", Animation.LoopType.LOOP));
            } else if (this.isEating()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_bison.eat", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_bison.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isCharging()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.charge", Animation.LoopType.LOOP));
            } else if (this.isPreparing()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.prepare", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.walk", Animation.LoopType.LOOP));
            } else if (this.isEating()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.eat", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_bison.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.bison.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_bison.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.bison.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.BISON.create(world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.BISON_FOR_BREEDING);
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.8F, 0.8F);
        }
        return this.getType().getDimensions();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 0.7F);
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

        if (!this.getWorld().isClient) {
            if (!this.isBaby() && this.chargeCooldown > 0) {
                this.chargeCooldown--;
            }

            // Looking/preparing phase
            if (this.isPreparing()) {
                if (this.lookingTimer > 0) {
                    this.lookingTimer--;
                }

                if (this.lookingTimer <= 0) {
                    this.runDirection = this.getRotationVec(1.0F);
                    this.setPreparing(false);
                    this.setCharging(true);
                    this.runningTimer = DEFAULT_RUNNING_TIME;
                    this.momentum = CHARGE_SPEED;
                    this.getNavigation().stop();
                }
            }

            // Charging phase
            if (this.isCharging()) {
                if (this.runningTimer > 0 && this.momentum > 0) {
                    this.runningTimer--;
                    Vec3d horizontal = this.runDirection.multiply(this.momentum);
                    this.setVelocity(horizontal.x, this.getVelocity().y, horizontal.z);

                    this.momentum -= MOMENTUM_DECREASE;
                    if (this.momentum < 0) this.momentum = 0;

                    //Solid block collision detection
                    BlockPos front = this.getBlockPos().add(
                            MathHelper.floor(this.runDirection.x * 1.5),
                            0,
                            MathHelper.floor(this.runDirection.z * 1.5)
                    );
                    BlockState state = this.getWorld().getBlockState(front);

                    if (!state.getCollisionShape(this.getWorld(), front).isEmpty()) {
                        this.setCharging(false);
                        this.setVelocity(Vec3d.ZERO);
                        this.chargeCooldown = this.DEFAULT_CHARGE_COOLDOWN;
                        return;
                    }

                    // Collision with entities
                    List<LivingEntity> hitEntities = this.getWorld().getEntitiesByClass(
                            LivingEntity.class,
                            this.getBoundingBox().expand(0.5),
                            e -> e != this && e.isAlive()
                    );

                    for (LivingEntity target : hitEntities) {
                        Vec3d knockback = target.getPos().subtract(this.getPos()).normalize().multiply(1.5);
                        target.takeKnockback(1.2, -knockback.x, -knockback.z);
                        target.damage(this.getDamageSources().mobAttack(this), 6.0f);
                        this.setCharging(false);
                        this.setVelocity(Vec3d.ZERO);
                        this.chargeCooldown = this.DEFAULT_CHARGE_COOLDOWN;
                        return;
                    }
                } else {
                    this.setCharging(false);
                    this.chargeCooldown = this.DEFAULT_CHARGE_COOLDOWN;
                    this.setVelocity(Vec3d.ZERO);
                }
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient && this.isCharging()) {
            Vec3d offset = new Vec3d(0, 0.1, 0);
            Vec3d forward = this.getRotationVec(1.0F).normalize();
            Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();

            Vec3d leftFoot = this.getPos().add(offset).add(right.multiply(-0.4));
            Vec3d rightFoot = this.getPos().add(offset).add(right.multiply(0.4));

            this.getWorld().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, leftFoot.x, leftFoot.y, leftFoot.z, 0, 0, 0);
            this.getWorld().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, rightFoot.x, rightFoot.y, rightFoot.z, 0, 0, 0);
        }

        if (this.isCharging() && !runDirection.equals(Vec3d.ZERO)) {
            double dx = runDirection.x;
            double dz = runDirection.z;
            float targetYaw = (float)(MathHelper.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;

            this.setYaw(targetYaw);
            this.prevYaw = targetYaw;

            this.setBodyYaw(targetYaw);
            this.prevBodyYaw = targetYaw;

            this.setHeadYaw(targetYaw);
            this.prevHeadYaw = targetYaw;
        }
    }

    public static class ChargeGoal extends Goal {
        private final BisonEntity bison;
        private PlayerEntity targetPlayer;
        private static final double DETECTION_RADIUS = 8.0;
        private static final int PROXIMITY_THRESHOLD = 200;

        public ChargeGoal(BisonEntity bison) {
            this.bison = bison;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.bison.isSleeping()) {
                return false;
            }

            if (this.bison.isTouchingWater()) {
                return false;
            }

            if (this.bison.isBaby()) {
                return false;
            }

            if (this.bison.chargeCooldown > 0) {
                return false;
            }

            this.targetPlayer = this.bison.getWorld().getClosestPlayer(
                    this.bison.getX(), this.bison.getY(), this.bison.getZ(),
                    DETECTION_RADIUS, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
            );

            if (this.targetPlayer == null || !this.bison.isOnGround()) {
                this.bison.proximityTimer = 0;
                return false;
            }

            if (this.targetPlayer.distanceTo(this.bison) <= DETECTION_RADIUS) {
                this.bison.proximityTimer++;
                return this.bison.proximityTimer >= PROXIMITY_THRESHOLD;
            } else {
                this.bison.proximityTimer = 0;
                return false;
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.bison.isPreparing() || this.bison.isCharging();
        }

        @Override
        public void start() {
            this.bison.setPreparing(true);
            this.bison.setCharging(false);
            this.bison.lookingTimer = this.bison.DEFAULT_LOOKING_TIME;
            this.bison.runDirection = Vec3d.ZERO;
            this.bison.proximityTimer = 0;
        }

        @Override
        public void stop() {
            this.bison.setCharging(false);
            this.bison.setPreparing(false);
            this.bison.setVelocity(Vec3d.ZERO);
            this.bison.velocityDirty = true;
            this.targetPlayer = null;
            this.bison.proximityTimer = 0;
        }

        @Override
        public void tick() {
            if (this.bison.isPreparing()) {
                if (this.bison.lookingTimer <= 0) {
                    this.bison.runDirection = this.bison.getRotationVec(1.0F);
                    this.bison.setPreparing(false);
                    this.bison.setCharging(true);
                    this.bison.getNavigation().stop();
                } else if (this.targetPlayer != null) {
                    double targetYaw = MathHelper.atan2(this.targetPlayer.getZ() - this.bison.getZ(), this.targetPlayer.getX() - this.bison.getX()) * (180F / Math.PI) - 90.0F;
                    this.bison.setRotation((float) targetYaw, this.bison.getPitch());
                    this.bison.setHeadYaw((float) targetYaw);
                    this.bison.prevYaw = (float) targetYaw;
                    this.bison.prevHeadYaw = (float) targetYaw;
                    this.bison.runDirection = this.bison.getRotationVec(1.0F);
                }
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.text.Normalizer;
import java.util.EnumSet;
import java.util.Random;

public class PenguinEntity extends AnimalEntity implements GeoEntity, Sleepy {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(PenguinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLIDING = DataTracker.registerData(PenguinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(PenguinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final EntityDimensions ADULT_STANDING = EntityDimensions.fixed(0.5f, 1.0f);
    private static final EntityDimensions BABY = EntityDimensions.fixed(0.5f, 0.7f);
    private static final EntityDimensions ADULT_SWIMMING = EntityDimensions.fixed(1.0f, 0.5f);

    private static final int SLIDE_COOLDOWN_DURATION = 20 * 60;
    private int slideCooldownTicks = 0;

    public PenguinEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new PenguinMoveControl();
        this.lookControl = new YawAdjustingLookControl(this, 8);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,false, true, true, false, 5.0, 500, 700, true, false, false, true, 1));
        this.goalSelector.add(0, new BabyFloatGoal(this));
        this.goalSelector.add(1, new PenguinMateGoal(this, 1.0));
        this.goalSelector.add(1, new PenguinLayEggGoal(this, 1.0));
        this.goalSelector.add(2, new PenguinSlideGoal(this));
        this.goalSelector.add(3, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(3, new FleeGoal<>(this, LivingEntity.class, ModTags.EntityTypes.PENGUIN_FLEE_FROM, 10.0f,1.0,1.2));
        this.goalSelector.add(4, new PenguinMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new SwimAroundGoal(this, 1.0, 10));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, CodEntity.class, true));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SLEEPING, false);
        builder.add(SLIDING, false);
        builder.add(HAS_EGG, false);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("Sliding", this.isSliding());
        nbt.putInt("SlideCooldown", this.getSlideCooldown());
        nbt.putBoolean("HasEgg", this.hasEgg());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setSliding(nbt.getBoolean("Sliding"));
        this.setSlideCooldown(nbt.getInt("SlideCooldown"));
        this.setHasEgg(nbt.getBoolean("HasEgg"));
    }

    public boolean hasEgg() {
        return this.dataTracker.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
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

    public boolean isSliding() {
        return this.dataTracker.get(SLIDING);
    }

    public void setSliding(boolean sliding) {
        this.dataTracker.set(SLIDING, sliding);
        if (sliding) {
            this.getNavigation().stop();
        }
    }

    public int getSlideCooldown() {
        return this.slideCooldownTicks;
    }

    public void setSlideCooldown(int ticks) {
        this.slideCooldownTicks = ticks;
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return BABY;
        }

        if (this.isTouchingWater() || this.isSliding() && !this.isBaby()) {
            return ADULT_SWIMMING;
        }

        return ADULT_STANDING;
    }

    protected EntityNavigation createNavigation(World world) {
        if (this.isBaby()) {
            return new MobNavigation(this, world);
        }

        return new AmphibiousSwimNavigation(this, world);
    }

    class PenguinMoveControl extends MoveControl {
        public PenguinMoveControl() {
            super(PenguinEntity.this);
        }
    }

    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (movementSpeed == null) return;

        if (this.isBaby()) {
            movementSpeed.setBaseValue(0.15f);
            return;
        }

        if (this.isOnGround() && !this.isBaby()) {
            movementSpeed.setBaseValue(0.15f);
        } else if (this.isTouchingWater()) {
            movementSpeed.setBaseValue(3.0f);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            if (this.getSlideCooldown() > 0) {
                this.setSlideCooldown(this.getSlideCooldown() - 1);
            }
        }

        if (!this.isBaby()) {
            if (this.isTouchingWater()) {
                this.moveControl = new AquaticMoveControl(this, 85, 10, 5F, 0.1F, true);
                this.calculateDimensions();
            } else if (this.isOnGround()) {
                this.moveControl = new PenguinMoveControl();
                this.calculateDimensions();
            }
        } else {
            if (!(this.moveControl instanceof PenguinMoveControl)) {
                this.moveControl = new PenguinMoveControl();
            }
        }

        if (this.getWorld().isClient()) {
            if (this.isTouchingWater() && this.getVelocity().horizontalLengthSquared() > 0.001D) {

                if (this.random.nextFloat() < 0.1F) {

                    Vec3d velocity = this.getVelocity().normalize().multiply(-0.5);
                    double yOffset = 0.1;

                    double xOffset = (this.random.nextDouble() - 0.5) * 0.4;
                    double zOffset = (this.random.nextDouble() - 0.5) * 0.4;

                    this.getWorld().addParticle(ParticleTypes.DOLPHIN,
                            this.getX() + velocity.x + xOffset,
                            this.getY() + yOffset,
                            this.getZ() + velocity.z + zOffset,
                            0.0, 0.0, 0.0);
                }
            }

            if (this.isSliding() && this.getVelocity().horizontalLengthSquared() > 0.01D) {
                Vec3d velocity = this.getVelocity().normalize().multiply(-0.3);

                BlockStateParticleEffect particleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.ICE.getDefaultState());

                for (int i = 0; i < 1; ++i) {
                    this.getWorld().addParticle(particleEffect,
                            this.getX() + velocity.x + (this.random.nextDouble() - 0.5) * 0.1,
                            this.getY() + 0.1,
                            this.getZ() + velocity.z + (this.random.nextDouble() - 0.5) * 0.1,
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.PENGUIN_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.PENGUIN.create(world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }

        if (this.isSliding()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.slide", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_penguin.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_penguin.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_penguin.swim", Animation.LoopType.LOOP));
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.idle_swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_penguin.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.penguin.sleep", Animation.LoopType.LOOP));
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
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }

        if (this.hasCustomName()) {
            String name = normalize(this.getName().getString().trim());

            if (name.equalsIgnoreCase("pingu") && this.random.nextInt(100) == 0 && !this.isBaby()) {
                return ModSounds.PENGUIN_NOOT_NOOT; //it's funny... don't ask me why... ¯\_(ツ)_/¯
            }
        }

        return ModSounds.PENGUIN_AMBIENT;
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.PENGUIN_HURT;
    }

    public static class PenguinSlideGoal extends Goal {
        private final PenguinEntity penguin;
        private final World world;
        private final Random random = new Random();

        private static final int SLIDE_DURATION_TICKS = 240;
        private static final double MIN_SPEED_SQ = 0.0005D; // Threshold to end slide

        private int slideTime;

        public PenguinSlideGoal(PenguinEntity penguin) {
            this.penguin = penguin;
            this.world = penguin.getWorld();
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {

            if (penguin.isSleeping()
                    || penguin.getSlideCooldown() > 0
                    || !penguin.isOnGround()
                    || penguin.isBaby()) {
                return false;
            }

            boolean canSlideOnBlockBelow =
                    this.world
                            .getBlockState(penguin.getBlockPos().down())
                            .isIn(ModTags.Blocks.PENGUIN_CAN_SLIDE_ON);

            if (canSlideOnBlockBelow) {
                return this.random.nextInt(100) < 50;
            }

            return false;
        }

        @Override
        public boolean shouldContinue() {

            double currentSpeedSq = penguin.getVelocity().horizontalLengthSquared();
            return this.slideTime > 0 && currentSpeedSq >= MIN_SPEED_SQ;
        }

        @Override
        public void start() {
            this.slideTime = SLIDE_DURATION_TICKS;
            penguin.setSliding(true);

            float yaw = penguin.getYaw();
            float forwardPush = 0.2f + this.random.nextFloat() * 0.2f;

            double velocityX = -Math.sin(yaw * 0.017453292F) * forwardPush;
            double velocityZ = Math.cos(yaw * 0.017453292F) * forwardPush;

            penguin.setVelocity(new Vec3d(velocityX, 0.0D, velocityZ));
        }

        @Override
        public void tick() {
            this.slideTime--;

            Vec3d velocity = penguin.getVelocity();
            double friction = 0.999D;

            penguin.setVelocity(velocity.x * friction, velocity.y, velocity.z * friction);

            penguin.move(net.minecraft.entity.MovementType.SELF, penguin.getVelocity());
        }

        @Override
        public void stop() {
            penguin.setSliding(false);
            penguin.setSlideCooldown(SLIDE_COOLDOWN_DURATION);
            penguin.setVelocity(Vec3d.ZERO);
        }
    }

    // baby penguins sometimes spin like crazy when going up... it's definitely not a bug... it's a feature
    public static class BabyFloatGoal extends Goal {
        private final PenguinEntity penguin;

        public BabyFloatGoal(PenguinEntity penguin) {
            this.penguin = penguin;
            this.setControls(EnumSet.of(Control.MOVE));
            penguin.getNavigation().setCanSwim(true);
        }

        @Override
        public boolean canStart() {
            return penguin.isBaby() && (penguin.isTouchingWater() && penguin.getFluidHeight(FluidTags.WATER) > penguin.getSwimHeight() || penguin.isInLava());
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (!penguin.getWorld().isClient()) {
                Vec3d vel = penguin.getVelocity();
                double targetUp = 0.12D;
                double newY = Math.max(vel.y, Math.min(targetUp, vel.y + 0.06D));
                penguin.setVelocity(vel.x * 0.92D, newY, vel.z * 0.92D);
                penguin.velocityModified = true;
            }
        }
    }

    static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final PenguinEntity penguin;

        public FleeGoal(PenguinEntity penguin, Class<T> fleeFromType, TagKey<EntityType<?>> fleeFromTag, float distance, double slowSpeed, double fastSpeed) {
            super(penguin, fleeFromType, distance, slowSpeed, fastSpeed,
                    (targetEntity) -> {
                        boolean isInTag = targetEntity != null && targetEntity.getType().isIn(fleeFromTag);
                        boolean passesDefaultChecks = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(targetEntity);
                        return isInTag && passesDefaultChecks;
                    });
            this.penguin = penguin;
        }

        @Override
        public boolean canStart() {
            return !this.penguin.isSleeping() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.penguin.isSleeping() && super.shouldContinue();
        }
    }

    private static class PenguinLayEggGoal extends MoveToTargetPosGoal {
        private final PenguinEntity penguin;

        PenguinLayEggGoal(PenguinEntity penguin, double speed) {
            super(penguin, speed, 16);
            this.penguin = penguin;
        }

        @Override
        public boolean canStart() {
            return penguin.hasEgg() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && penguin.hasEgg();
        }

        @Override
        public void tick() {
            super.tick();

            if (!this.hasReached()) {
                return;
            }

            World world = penguin.getWorld();
            BlockPos eggPos = this.targetPos.up(); // up(1)

            if (!world.getBlockState(eggPos).isAir()) {
                return;
            }

            world.playSound(
                    null,
                    eggPos,
                    SoundEvents.ENTITY_TURTLE_LAY_EGG,
                    SoundCategory.BLOCKS,
                    0.3F,
                    0.9F + world.random.nextFloat() * 0.2F
            );

            world.setBlockState(eggPos, ModBlocks.PENGUIN_EGG.getDefaultState(), 3);

            penguin.setHasEgg(false);
            penguin.setLoveTicks(600);
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isIn(ModTags.Blocks.PENGUIN_EGG_HATCHABLE);
        }
    }

    private static class PenguinMateGoal extends AnimalMateGoal {
        private final PenguinEntity penguin;

        PenguinMateGoal(PenguinEntity penguin, double speed) {
            super(penguin, speed);
            this.penguin = penguin;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !penguin.hasEgg();
        }

        @Override
        protected void breed() {
            if (mate == null) {
                return;
            }

            ServerPlayerEntity serverPlayerEntity = animal.getLovingPlayer();
            if (serverPlayerEntity == null) {
                serverPlayerEntity = mate.getLovingPlayer();
            }

            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, animal, mate, null);
            }

            // Assign the egg to one random penguin
            if (animal.getRandom().nextBoolean()) {
                penguin.setHasEgg(true);
            } else {
                ((PenguinEntity) mate).setHasEgg(true);
            }

            animal.setBreedingAge(6000);
            mate.setBreedingAge(6000);
            animal.resetLoveTicks();
            mate.resetLoveTicks();

            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                var random = animal.getRandom();
                world.spawnEntity(
                        new ExperienceOrbEntity(
                                world,
                                animal.getX(),
                                animal.getY(),
                                animal.getZ(),
                                random.nextInt(7) + 1
                        )
                );
            }
        }
    }
}

package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.CrocodileEggBlock;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.CrocodileMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.client.crocodile.CrocodileVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CrocodileEntity extends AnimalEntity implements GeoEntity, Sleepy {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Boolean> OVERGROWN = DataTracker.registerData(CrocodileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(CrocodileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(CrocodileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(CrocodileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int waterTimer = 0; // Timer for time spent in water
    private final int overgrownTimer; // Time in water before becoming overgrown

    public CrocodileEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new CrocodileMoveControl();
        this.overgrownTimer = Random.create().nextBetween(1000, 1200);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, false, true, true, false, 3.0, 500, 700, true, false, false, true));
        this.goalSelector.add(1, new CrocodileMeleeAttackGoal(this, 1.0, true, 2));
        this.goalSelector.add(1, new CrocodileMateGoal(this, 1.0));
        this.goalSelector.add(1, new CrocodileLayEggGoal(this, 1.0));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, SalmonEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, CodEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    class CrocodileMoveControl extends MoveControl {
        public CrocodileMoveControl() {
            super(CrocodileEntity.this);
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (this.isBaby()) {
                // Baby speed values
                if (this.isOnGround()) {
                    movementSpeed.setBaseValue(0.15f);
                } else if (this.isTouchingWater()) {
                    movementSpeed.setBaseValue(1.2f);
                }
            } else {
                // Adult speed values
                if (this.isOnGround()) {
                    movementSpeed.setBaseValue(0.2f);
                } else if (this.isTouchingWater()) {
                    movementSpeed.setBaseValue(1.5f);
                }
            }
        }
    }

    public boolean canBreatheInWater() {
        return true;
    }

    public boolean isPushedByFluids() {
        return false;
    }

    protected EntityNavigation createNavigation(World world) {
        return new AmphibiousSwimNavigation(this, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isTouchingWater()) {
            this.moveControl = new AquaticMoveControl(this, 85, 10, 5F, 0.1F, true);
        } else if (this.isOnGround()) {
            this.moveControl = new CrocodileMoveControl();
        }

        if (this.isSleeping()) {
            this.getNavigation().stop();
            return;
        }

        if (this.isTouchingWater() && !this.isBaby()) {
            if (!this.isOvergrown()) {
                this.waterTimer++;
                if (this.waterTimer >= this.overgrownTimer) {
                    this.setOvergrown(true);
                }
            }
        } else {
            if (!this.isOvergrown()) {
                this.waterTimer = 0;
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.isOvergrown() && itemStack.isOf(Items.SHEARS)) {
            this.setOvergrown(false);
            this.waterTimer = 0;

            Random random = Random.create();
            int mossCarpetAmount = random.nextBetween(1, 5);
            for (int i = 0; i < mossCarpetAmount; i++) {
                this.dropItem(Items.MOSS_CARPET);
            }

            if (random.nextFloat() < 0.5F) {
                int scaleAmount = random.nextBetween(1, 3);
                if (this.getVariant() == CrocodileVariants.ALBINO) {
                    for (int i = 0; i < scaleAmount; i++) {
                        this.dropItem(ModItems.CROCODILE_SCALE_ALBINO);
                    }
                } else {
                    for (int i = 0; i < scaleAmount; i++) {
                        this.dropItem(ModItems.CROCODILE_SCALE);
                    }
                }
            }

            itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
            this.emitGameEvent(GameEvent.SHEAR, player);
            this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
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

    public void setOvergrown(boolean isOvergrown) {
        this.getDataTracker().set(OVERGROWN, isOvergrown);
    }

    public boolean isOvergrown() {
        return this.getDataTracker().get(OVERGROWN);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OVERGROWN, false);
        this.dataTracker.startTracking(HAS_EGG, false);
        this.dataTracker.startTracking(SLEEPING, false);

        this.dataTracker.startTracking(VARIANT, CrocodileVariants.NORMAL.getId());
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.CROCODILE.create(world);
    }

    public CrocodileVariants getVariant() {
        return CrocodileVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(CrocodileVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(CrocodileVariants.ALBINO);
        } else {
            this.setVariant(CrocodileVariants.NORMAL);
        }
        return entityData;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("WaterTimer", this.waterTimer);
        nbt.putBoolean("IsOvergrown", this.isOvergrown());
        nbt.putBoolean("HasEgg", this.hasEgg());
        nbt.putBoolean("Sleeping", this.isSleeping());

        nbt.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.waterTimer = nbt.getInt("WaterTimer");
        this.setOvergrown(nbt.getBoolean("IsOvergrown"));
        this.setHasEgg(nbt.getBoolean("HasEgg"));
        this.setSleeping(nbt.getBoolean("Sleeping"));

        this.setVariant(CrocodileVariants.byId(nbt.getInt("Variant")));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.75F, 0.35F);
        }
        return super.getDimensions(pose);
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

        if (this.isBaby()) {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crocodile.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crocodile.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crocodile.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crocodile.idle", Animation.LoopType.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crocodile.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crocodile.sleep", Animation.LoopType.LOOP));
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
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crocodile.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_crocodile.idle_swim", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crocodile.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.crocodile.idle_swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.CROCODILE_FOR_BREEDING);
    }

    public boolean hasEgg() {
        return this.dataTracker.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    private static class CrocodileLayEggGoal extends MoveToTargetPosGoal {
        private final CrocodileEntity crocodile;

        CrocodileLayEggGoal(CrocodileEntity crocodile, double speed) {
            super(crocodile, speed, 16);
            this.crocodile = crocodile;
        }

        @Override
        public boolean canStart() {
            return this.crocodile.hasEgg() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.crocodile.hasEgg();
        }

        @Override
        public void tick() {
            super.tick();
            BlockPos blockPos = this.crocodile.getBlockPos();

            if (this.hasReached()) {
                World world = this.crocodile.getWorld();
                world.playSound(null, blockPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);

                int eggCount = world.random.nextInt(3) + 1;

                BlockPos eggPos = this.targetPos.up(1);
                BlockState eggState = ModBlocks.CROCODILE_EGG.getDefaultState().with(CrocodileEggBlock.EGGS, eggCount);
                world.setBlockState(eggPos, eggState, 3);


                this.crocodile.setHasEgg(false);
                this.crocodile.setLoveTicks(600);
            }
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isIn(ModTags.Blocks.CROCODILE_EGG_HATCHABLE);
        }
    }

    private static class CrocodileMateGoal extends AnimalMateGoal {
        private final CrocodileEntity crocodile;

        CrocodileMateGoal(CrocodileEntity crocodile, double speed) {
            super(crocodile, speed);
            this.crocodile = crocodile;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.crocodile.hasEgg();
        }

        @Override
        protected void breed() {
            ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
            if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
                serverPlayerEntity = this.mate.getLovingPlayer();
            }

            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, this.animal, this.mate, (PassiveEntity)null);
            }

            // Assign the egg to one random crocodile
            if (this.animal.getRandom().nextBoolean()) {
                this.crocodile.setHasEgg(true);
            } else {
                ((CrocodileEntity) this.mate).setHasEgg(true);
            }

            this.animal.setBreedingAge(6000);
            this.mate.setBreedingAge(6000);
            this.animal.resetLoveTicks();
            this.mate.resetLoveTicks();

            Random random = this.animal.getRandom();
            if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }
        }
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return ModSounds.CROCODILE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CROCODILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CROCODILE_DEATH;
    }
}
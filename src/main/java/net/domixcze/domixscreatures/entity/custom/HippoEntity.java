package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.HippoMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.WanderAroundGoal;
import net.domixcze.domixscreatures.entity.client.hippo.HippoVariants;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
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

import java.util.EnumSet;

public class HippoEntity extends AnimalEntity implements GeoEntity, Sleepy {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(HippoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_ROLLING_IN_MUD = DataTracker.registerData(HippoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_MUDDY = DataTracker.registerData(HippoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_MUD_DRY = DataTracker.registerData(HippoEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HippoEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private int mudDryingTimer = 0;
    private int mudCooldown = 0;
    private int mudWashingTimer = 0;

    public HippoEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new HippoMoveControl();
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, true, false, false, false, 3.0, 700, 800, true, false, false, true));
        this.goalSelector.add(1, new HippoMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(1, new RollInMudGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, CrocodileEntity.class, true));
    }

    protected EntityNavigation createNavigation(World world) {
        return new AmphibiousSwimNavigation(this, world);
    }

    class HippoMoveControl extends MoveControl {
        public HippoMoveControl() {
            super(HippoEntity.this);
        }
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    public boolean isPushable() {
        return false;
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.HIPPO_FOR_BREEDING);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(HippoVariants.ALBINO);
        } else {
            this.setVariant(HippoVariants.NORMAL);
        }
        return entityData;
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(1.7F, 1.5F);
        }
        return this.getType().getDimensions();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        HippoEntity baby = (HippoEntity) ModEntities.HIPPO.create(world);
        if (baby != null) {
            HippoEntity parent1 = this;
            HippoEntity parent2 = (HippoEntity) entity;

            if ((parent1.getVariant() == HippoVariants.ALBINO) && (parent2.getVariant() == HippoVariants.ALBINO)) {
                baby.setVariant(HippoVariants.ALBINO);
            } else if (parent1.getVariant() == HippoVariants.ALBINO || parent2.getVariant() == HippoVariants.ALBINO) {
                double chance = 0.25;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(HippoVariants.ALBINO);
                } else {
                    baby.setVariant(HippoVariants.NORMAL);
                }
            } else {
                double chance = 0.01;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(HippoVariants.ALBINO);
                } else {
                    baby.setVariant(HippoVariants.NORMAL);
                }
            }
        }
        return baby;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, HippoVariants.NORMAL.getId());
        builder.add(SLEEPING, false);
        builder.add(IS_ROLLING_IN_MUD, false);
        builder.add(IS_MUDDY, false);
        builder.add(IS_MUD_DRY, false);
    }

    public boolean isRollingInMud() {
        return this.dataTracker.get(IS_ROLLING_IN_MUD);
    }

    public void setRollingInMud(boolean rolling) {
        this.dataTracker.set(IS_ROLLING_IN_MUD, rolling);
    }

    public boolean isMuddy() {
        return this.dataTracker.get(IS_MUDDY);
    }

    public void setMuddy(boolean muddy) {
        this.dataTracker.set(IS_MUDDY, muddy);
    }

    public boolean isMudDry() {
        return this.dataTracker.get(IS_MUD_DRY);
    }

    public void setMudDry(boolean mudDry) {
        this.dataTracker.set(IS_MUD_DRY, mudDry);
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
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("MudCooldown", mudCooldown);
        nbt.putInt("MudWashTimer", this.mudWashingTimer);
        nbt.putBoolean("IsMuddy", this.isMuddy());
        nbt.putInt("MudDryingTimer", this.mudDryingTimer);
        nbt.putBoolean("IsMudDry", this.isMudDry());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(HippoVariants.byId(nbt.getInt("Variant")));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        mudCooldown = nbt.getInt("MudCooldown");
        this.mudWashingTimer = nbt.getInt("MudWashTimer");
        this.setMuddy(nbt.getBoolean("IsMuddy"));
        this.mudDryingTimer = nbt.getInt("MudDryingTimer");
        this.setMudDry(nbt.getBoolean("IsMudDry"));
    }

    public HippoVariants getVariant() {
        return HippoVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(HippoVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (super.damage(source, amount)) {
            this.setSleeping(false);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.isSleeping()) {
            this.getNavigation().stop();
        }
        super.tick();

        if (this.isTouchingWaterOrRain()) {
            if (this.isMuddy()) {
                mudWashingTimer++;

                if (mudWashingTimer >= 200) {
                    this.setMuddy(false);
                    mudWashingTimer = 0;
                }
            }
        } else {
            mudWashingTimer = 0;
        }

        if (mudCooldown > 0) {
            mudCooldown--;
        }

        if (this.isMuddy()) {
            if (!this.isMudDry()) {
                mudDryingTimer++;
                if (mudDryingTimer >= 600) {
                    this.setMudDry(true);
                    mudDryingTimer = 0;
                }
            }
        } else {
            mudDryingTimer = 0;
            this.setMudDry(false);
        }

        EntityAttributeInstance armorAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        if (armorAttribute != null) {
            if (this.isMudDry()) {
                armorAttribute.setBaseValue(5.0);
            } else {
                armorAttribute.setBaseValue(0.0);
            }
        }

        if (this.isTouchingWater()) {
            this.moveControl = new AquaticMoveControl(this, 85, 10, 5F, 0.1F, true);
        } else if (this.isOnGround()) {
            this.moveControl = new HippoMoveControl();
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (this.isOnGround()) {
                movementSpeed.setBaseValue(0.2f);
            } else if (this.isTouchingWater()) {
                movementSpeed.setBaseValue(1.5f);
            }
        }
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
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.walk", Animation.LoopType.LOOP));
            } else if (this.isRollingInMud()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.roll", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.walk", Animation.LoopType.LOOP));
            } else if (this.isRollingInMud()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.roll", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.idle_swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.idle_swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.hippo.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return ModSounds.HIPPO_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.HIPPO_HURT;
    }

    public static class RollInMudGoal extends Goal {
        private final HippoEntity hippo;
        private int rollingTime;
        private boolean fullyRolled = false;
        private int soundCooldown = 10;

        public RollInMudGoal(HippoEntity hippo) {
            this.hippo = hippo;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.hippo.mudCooldown > 0 || this.hippo.isSleeping()) {
                return false;
            }

            BlockPos blockPos = this.hippo.getBlockPos().down();
            BlockState blockState = this.hippo.getWorld().getBlockState(blockPos);
            return blockState.isOf(Blocks.MUD) && !this.hippo.isMuddy();
        }

        @Override
        public void start() {
            rollingTime = 400; // 20 seconds
            fullyRolled = false;
            this.hippo.setRollingInMud(true);
            this.hippo.getNavigation().stop();
        }

        @Override
        public void stop() {
            this.hippo.setRollingInMud(false);

            if (fullyRolled) {
                this.hippo.setMuddy(true);
                this.hippo.mudCooldown = 600;
            }
        }

        @Override
        public boolean shouldContinue() {
            BlockPos blockPos = this.hippo.getBlockPos().down();
            BlockState blockState = this.hippo.getWorld().getBlockState(blockPos);

            if (!blockState.isOf(Blocks.MUD)) {
                rollingTime = 0;
                return false;
            }

            return rollingTime > 0;
        }

        @Override
        public void tick() {
            rollingTime--;

            this.hippo.getNavigation().stop();
            this.hippo.getLookControl().tick();

            if (this.hippo.isRollingInMud()) {
                spawnMudParticles();

                if (soundCooldown <= 0) {
                    this.hippo.getWorld().playSound(
                            null,
                            this.hippo.getX(),
                            this.hippo.getY(),
                            this.hippo.getZ(),
                            SoundEvents.BLOCK_MUD_STEP,
                            this.hippo.getSoundCategory(),
                            1.0F,
                            1.0F
                    );
                    soundCooldown = 10;
                } else {
                    soundCooldown--;
                }
            }

            if (rollingTime <= 0) {
                fullyRolled = true;
                stop();
            }
        }

        private void spawnMudParticles() {
            if (this.hippo.getWorld() instanceof ServerWorld serverWorld) {
                double centerX = this.hippo.getX();
                double centerY = this.hippo.getY();
                double centerZ = this.hippo.getZ();

                BlockStateParticleEffect mudParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.MUD.getDefaultState());

                for (int i = 0; i < 20; i++) {
                    double offsetX = (this.hippo.getRandom().nextDouble() - 0.5) * 1.5;
                    double offsetY = this.hippo.getRandom().nextDouble() * 0.5;
                    double offsetZ = (this.hippo.getRandom().nextDouble() - 0.5) * 1.5;

                    serverWorld.spawnParticles(mudParticleEffect,
                            centerX + offsetX,
                            centerY + offsetY,
                            centerZ + offsetZ,
                            1, 0, 0, 0, 0.1);
                }
            }
        }
    }
}

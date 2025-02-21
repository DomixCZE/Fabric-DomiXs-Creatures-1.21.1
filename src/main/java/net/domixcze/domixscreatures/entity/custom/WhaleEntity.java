package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ai.Beachable;
import net.domixcze.domixscreatures.entity.ai.BeachedGoal;
import net.domixcze.domixscreatures.entity.client.whale.WhaleVariants;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WhaleEntity extends WaterCreatureEntity implements GeoEntity, Beachable {

    private static final TrackedData<Integer> BARNACLE_COUNT = DataTracker.registerData(WhaleEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> BEACHED = DataTracker.registerData(WhaleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(WhaleEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int BARNACLE_REGEN_COOLDOWN = 12000;
    private int barnacleRegenTimer = BARNACLE_REGEN_COOLDOWN;

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public WhaleEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 70, 5, 0.01F, 0.05F, true);
        this.lookControl = new YawAdjustingLookControl(this, 8);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BeachedGoal(this, this));
        this.goalSelector.add(1, new WhaleJumpGoal(this, 10));
        this.goalSelector.add(2, new SwimAroundGoal(this, 0.8, 12));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(WhaleVariants.ALBINO);
        } else {
            this.setVariant(WhaleVariants.NORMAL);
        }

        return entityData;
    }

    public WhaleVariants getVariant() {
        return WhaleVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(WhaleVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> whaleAnimationState) {
        if (whaleAnimationState.isMoving() && this.isTouchingWater()) {
            whaleAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.whale.swim", Animation.LoopType.LOOP));
        } else if (isBeached()){
            whaleAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.whale.beached", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            whaleAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.whale.idle_swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, WhaleVariants.NORMAL.ordinal());
        this.dataTracker.startTracking(BEACHED, false);
        this.dataTracker.startTracking(BARNACLE_COUNT, 0);
    }

    public boolean isBeached() {
        return this.dataTracker.get(BEACHED);
    }

    public void setBeached(boolean beached) {
        this.dataTracker.set(BEACHED, beached);
    }

    public int getBarnacleCount() {
        return this.dataTracker.get(BARNACLE_COUNT);
    }

    public void setBarnacleCount(int number) {
        this.dataTracker.set(BARNACLE_COUNT, number);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putBoolean("Beached", this.isBeached());
        nbt.putInt("BarnacleCount", this.getBarnacleCount());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(WhaleVariants.values()[nbt.getInt("Variant")]);
        this.setBeached(nbt.getBoolean("Beached"));
        this.setBarnacleCount(nbt.getInt("BarnacleCount"));
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.SHEARS)) {
            if (!this.getWorld().isClient) {
                int currentNumber = this.getBarnacleCount();

                if (currentNumber > 0) {
                    this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    this.emitGameEvent(GameEvent.SHEAR, player);

                    this.dropItem(ModBlocks.BARNACLE_BLOCK);
                    this.setBarnacleCount(currentNumber - 1);

                    itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));

                    return ActionResult.SUCCESS;
                }
            } else {
                return ActionResult.CONSUME;
            }
        }
        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (this.getBarnacleCount() < 9) {
                if (barnacleRegenTimer > 0) {
                    barnacleRegenTimer--;
                } else {
                    this.setBarnacleCount(this.getBarnacleCount() + 1);
                    barnacleRegenTimer = BARNACLE_REGEN_COOLDOWN;
                }
            }
        }
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isBeached()) {
            return null;
        }
        return ModSounds.WHALE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WHALE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.WHALE_DEATH;
    }

    private static class WhaleJumpGoal extends DiveJumpingGoal {
        private static final int[] OFFSET_MULTIPLIERS = new int[]{0, 2, 6, 8, 10, 12};
        private final WhaleEntity whale;
        private final int chance;
        private boolean inWater;

        public WhaleJumpGoal(WhaleEntity whale, int chance) {
            this.whale = whale;
            this.chance = toGoalTicks(chance);
        }

        public boolean canStart() {
            if (this.whale.getRandom().nextInt(this.chance) != 0) {
                return false;
            } else {
                Direction direction = this.whale.getMovementDirection();
                int i = direction.getOffsetX();
                int j = direction.getOffsetZ();
                BlockPos blockPos = this.whale.getBlockPos();
                int[] var5 = OFFSET_MULTIPLIERS;
                int var6 = var5.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    int k = var5[var7];
                    if (!this.isWater(blockPos, i, j, k) || !this.isAirAbove(blockPos, i, j, k)) {
                        return false;
                    }
                }

                return true;
            }
        }

        private boolean isWater(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
            BlockPos blockPos = pos.add(offsetX * multiplier, 0, offsetZ * multiplier);
            return this.whale.getWorld().getFluidState(blockPos).isIn(FluidTags.WATER) && !this.whale.getWorld().getBlockState(blockPos).blocksMovement();
        }

        private boolean isAirAbove(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
            return this.whale.getWorld().getBlockState(pos.add(offsetX * multiplier, 1, offsetZ * multiplier)).isAir() && this.whale.getWorld().getBlockState(pos.add(offsetX * multiplier, 2, offsetZ * multiplier)).isAir();
        }

        public boolean shouldContinue() {
            double d = this.whale.getVelocity().y;
            return (!(d * d < 0.029999999329447746) || this.whale.getPitch() == 0.0F || !(Math.abs(this.whale.getPitch()) < 10.0F) || !this.whale.isTouchingWater()) && !this.whale.isOnGround();
        }

        public boolean canStop() {
            return false;
        }

        public void start() {
            Direction direction = this.whale.getMovementDirection();
            this.whale.setVelocity(this.whale.getVelocity().add((double)direction.getOffsetX() * 0.8, 0.9, (double)direction.getOffsetZ() * 1.2));
            this.whale.getNavigation().stop();
        }

        public void stop() {
            this.whale.setPitch(0.0F);
        }

        public void tick() {
            boolean bl = this.inWater;
            if (!bl) {
                FluidState fluidState = this.whale.getWorld().getFluidState(this.whale.getBlockPos());
                this.inWater = fluidState.isIn(FluidTags.WATER);
            }

            if (this.inWater && !bl) {
                this.whale.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
            }

            Vec3d vec3d = this.whale.getVelocity();
            if (vec3d.y * vec3d.y < 0.05 && Math.abs(this.whale.getPitch()) > 0.5F) {
                this.whale.setPitch(MathHelper.lerpAngleDegrees(0.1F, this.whale.getPitch(), 0.0F));
            } else if (vec3d.length() > 9.999999747378752E-6) {
                double d = vec3d.horizontalLength();
                double e = Math.atan2(-vec3d.y, d) * 57.2957763671875;
                this.whale.setPitch((float)e);
            }
        }
    }
}
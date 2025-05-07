package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class WaterStriderEntity extends TameableEntity implements GeoEntity, Saddleable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(WaterStriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public WaterStriderEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new GoBackToWaterGoal(this, 1.0));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SADDLED, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 2, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 2, this::waterPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.water_strider.walk", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.water_strider.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.water_strider.walk_water", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.water_strider.idle_water", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean canWalkOnFluid(FluidState state) {
        return state.isIn(FluidTags.WATER);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }


    @Override
    public boolean canBeSaddled() {
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void saddle(ItemStack stack, @Nullable SoundCategory sound) {
        this.dataTracker.set(SADDLED, true);
        if (sound != null) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), this.getSaddleSound(), sound, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.dataTracker.get(SADDLED);
    }

    public void unsaddle() {
        this.dataTracker.set(SADDLED, false);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Saddled", this.isSaddled());
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SADDLED, nbt.getBoolean("Saddled"));
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.isSaddled() ? (LivingEntity) this.getFirstPassenger() : null;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.hasPassengers() && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity livingentity = this.getControllingPassenger();
            this.setYaw(livingentity.getYaw());
            this.prevYaw = this.getYaw();
            this.setPitch(livingentity.getPitch() * 0.5F);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float forward = livingentity.forwardSpeed;
            float sideways = livingentity.sidewaysSpeed;
            float speedMultiplier;

            if (this.isTouchingWater()) {
                speedMultiplier = 1.3F; // water speed
            } else {
                speedMultiplier = 0.6F; // land speed
            }

            this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * speedMultiplier);
            super.travel(new Vec3d(sideways * speedMultiplier, movementInput.y, forward * speedMultiplier));
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.getItem() == ModItems.WORM && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }

        boolean isSaddle = itemStack.isOf(Items.SADDLE);

        if (!this.isAlive()) {
            return ActionResult.PASS;
        }

        if (isSaddle && this.canBeSaddled() && !this.isSaddled() && this.isTamed()) {
            this.saddle(itemStack, SoundCategory.NEUTRAL);
            this.setPersistent();
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        } else if (this.isSaddled() && itemStack.isEmpty() && player.isSneaking()) {
            this.dropItem(Items.SADDLE);
            this.unsaddle();
            return ActionResult.SUCCESS;
        } else if (this.isSaddled() && !player.hasVehicle()) {
            player.startRiding(this);
            return ActionResult.success(this.getWorld().isClient);
        } else if (!this.isSaddled() && !player.hasVehicle() && itemStack.isEmpty()) {
            return ActionResult.PASS;
        }

        return super.interactMob(player, hand);
    }

    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        double xOffset = 0.0; // backward
        double yOffset = 0.8; // up and down
        double zOffset = 0.0; // forward
        Vec3d offsetVector = (new Vec3d(xOffset, yOffset, zOffset)).rotateY(-this.bodyYaw * 0.017453292F);
        positionUpdater.accept(passenger, this.getX() + offsetVector.x, this.getY() + offsetVector.y, this.getZ() + offsetVector.z);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new WaterStriderEntity.WaterStriderNavigation(this, world);
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        if (world.getBlockState(pos).getFluidState().isIn(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return this.isTouchingWater() ? Float.NEGATIVE_INFINITY : 0.0F;
        }
    }

    static class GoBackToWaterGoal extends MoveToTargetPosGoal {
        private final WaterStriderEntity strider;

        GoBackToWaterGoal(WaterStriderEntity strider, double speed) {
            super(strider, speed, 8, 2);
            this.strider = strider;
        }

        @Override
        public BlockPos getTargetPos() {
            return this.targetPos;
        }

        @Override
        public boolean shouldContinue() {
            return !this.strider.isTouchingWater() && this.isTargetPos(this.strider.getWorld(), this.targetPos);
        }

        @Override
        public boolean canStart() {
            return !this.strider.isTouchingWater() && super.canStart();
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 20 == 0;
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isOf(Blocks.WATER) && world.getBlockState(pos.up()).canPathfindThrough(NavigationType.LAND);
        }
    }

    public static class WaterStriderNavigation extends MobNavigation {
        public WaterStriderNavigation(MobEntity entity, World world) {
            super(entity, world);
        }

        @Override
        protected PathNodeNavigator createPathNodeNavigator(int range) {
            this.nodeMaker = new LandPathNodeMaker();
            return new PathNodeNavigator(this.nodeMaker, range);
        }

        @Override
        protected boolean canWalkOnPath(PathNodeType pathType) {
            return pathType == PathNodeType.WATER || super.canWalkOnPath(pathType);
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            FluidState fluidState = this.world.getFluidState(pos);
            return fluidState.isIn(FluidTags.WATER) || super.isValidPosition(pos);
        }
    }
}
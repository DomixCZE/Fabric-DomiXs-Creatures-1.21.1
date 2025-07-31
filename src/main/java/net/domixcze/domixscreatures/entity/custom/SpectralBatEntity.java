package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.ScreechAttackGoal;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpectralBatEntity extends AnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Boolean> SCREECHING = DataTracker.registerData(SpectralBatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> BAT_FLAGS = DataTracker.registerData(SpectralBatEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final int HANGING_FLAG = 1;
    private static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(4.0);

    private int screechCooldown;
    private int screechDuration;

    @Nullable
    private BlockPos hangingPosition;

    public SpectralBatEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(2, new FlyGoal(this, 1.0));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(3,new ScreechAttackGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BAT_FLAGS, (byte)0);
        builder.add(SCREECHING, false);
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isHanging();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.SPECTRAL_BAT_FOR_BREEDING);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.SPECTRAL_BAT.create(world);
    }

    public void setScreeching(boolean screeching) {
        this.dataTracker.set(SCREECHING, screeching);
    }

    public boolean isScreeching() {
        return this.dataTracker.get(SCREECHING);
    }

    public void resetScreechCooldown() {
        this.screechCooldown = 400;
    }

    public int getScreechCooldown() {
        return this.screechCooldown;
    }

    public void setScreechCooldown(int cooldown) {
        this.screechCooldown = cooldown;
    }

    public void resetScreechDuration() {
        this.screechDuration = 40;
    }

    public int getScreechDuration() {
        return this.screechDuration;
    }

    public void setScreechDuration(int duration) {
        this.screechDuration = duration;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.isBaby()) {
            if (this.isHanging()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_spectral_bat.hang", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_spectral_bat.fly", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isHanging()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.spectral_bat.hang", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.spectral_bat.fly", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isHanging()) {
            this.setVelocity(Vec3d.ZERO);
            this.setPos(this.getX(), MathHelper.floor(this.getY()) + 1.0 - this.getHeight(), this.getZ());
        } else {
            this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
        }

        if (this.isScreeching() && this.getScreechDuration() > 0) {
            this.setScreechDuration(this.getScreechDuration() - 1);
        } else {
            this.setScreeching(false);
        }

        if (this.getScreechCooldown() > 0) {
            this.setScreechCooldown(this.getScreechCooldown() - 1);
        }
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        BlockPos blockPos = this.getBlockPos();
        BlockPos blockPosAbove = blockPos.up();

        if (this.isHanging()) {
            if (this.getWorld().getBlockState(blockPosAbove).isSolidBlock(this.getWorld(), blockPosAbove)) {
                if (this.random.nextInt(200) == 0) {
                    this.headYaw = (float)this.random.nextInt(360);
                }
                if (this.getWorld().getClosestPlayer(CLOSE_PLAYER_PREDICATE, this) != null) {
                    this.setHanging(false);
                }
            } else {
                this.setHanging(false);
            }
        } else {
            if (this.hangingPosition != null && (!this.getWorld().isAir(this.hangingPosition) || this.hangingPosition.getY() <= this.getWorld().getBottomY())) {
                this.hangingPosition = null;
            }

            if (this.hangingPosition == null || this.random.nextInt(30) == 0 || this.hangingPosition.isWithinDistance(this.getPos(), 2.0)) {
                this.hangingPosition = BlockPos.ofFloored(
                        this.getX() + this.random.nextInt(7) - this.random.nextInt(7),
                        this.getY() + this.random.nextInt(6) - 2,
                        this.getZ() + this.random.nextInt(7) - this.random.nextInt(7)
                );
            }

            if (this.hangingPosition != null) {
                double dx = this.hangingPosition.getX() + 0.5 - this.getX();
                double dy = this.hangingPosition.getY() + 0.1 - this.getY();
                double dz = this.hangingPosition.getZ() + 0.5 - this.getZ();
                Vec3d velocity = this.getVelocity();
                Vec3d newVelocity = velocity.add(
                        (Math.signum(dx) * 0.5 - velocity.x) * 0.1F,
                        (Math.signum(dy) * 0.7F - velocity.y) * 0.1F,
                        (Math.signum(dz) * 0.5 - velocity.z) * 0.1F
                );
                this.setVelocity(newVelocity);
                float yaw = (float)(MathHelper.atan2(newVelocity.z, newVelocity.x) * 180.0 / Math.PI) - 90.0F;
                float h = MathHelper.wrapDegrees(yaw - this.getYaw());
                this.forwardSpeed = 0.5F;
                this.setYaw(this.getYaw() + h);

                if (this.random.nextInt(100) == 0 && this.getWorld().getBlockState(blockPosAbove).isSolidBlock(this.getWorld(), blockPosAbove)) {
                    this.setHanging(true);
                }
            }
        }
    }

    public boolean isHanging() {
        return (this.dataTracker.get(BAT_FLAGS) & HANGING_FLAG) != 0;
    }

    public void setHanging(boolean hanging) {
        byte flags = this.dataTracker.get(BAT_FLAGS);
        if (hanging) {
            this.dataTracker.set(BAT_FLAGS, (byte)(flags | HANGING_FLAG));
        } else {
            this.dataTracker.set(BAT_FLAGS, (byte)(flags & ~HANGING_FLAG));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(BAT_FLAGS, nbt.getByte("BatFlags"));
        this.dataTracker.set(SCREECHING, nbt.getBoolean("isScreeching"));
        this.screechCooldown = nbt.getInt("ScreechCooldown");
        this.screechDuration = nbt.getInt("ScreechDuration");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("BatFlags", this.dataTracker.get(BAT_FLAGS));
        nbt.putBoolean("isScreeching", this.dataTracker.get(SCREECHING));
        nbt.putInt("ScreechCooldown", this.screechCooldown);
        nbt.putInt("ScreechDuration", this.screechDuration);
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BAT_DEATH;
    }
}
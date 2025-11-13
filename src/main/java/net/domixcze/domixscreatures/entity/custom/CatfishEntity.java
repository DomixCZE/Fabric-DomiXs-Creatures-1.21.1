package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.Beachable;
import net.domixcze.domixscreatures.entity.ai.BeachedGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CatfishEntity extends WaterCreatureEntity implements GeoEntity, Beachable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> BEACHED = DataTracker.registerData(CatfishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public CatfishEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 70, 5, 0.01F, 0.05F, true);
        this.lookControl = new YawAdjustingLookControl(this, 8);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BeachedGoal(this, this));
        this.goalSelector.add(1, new SwimAroundGoal(this, 0.8, 12));
        this.goalSelector.add(2, new LookAroundGoal(this));
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BEACHED, false);
    }

    public boolean isBeached() {
        return this.dataTracker.get(BEACHED);
    }

    public void setBeached(boolean beached) {
        this.dataTracker.set(BEACHED, beached);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Beached", this.isBeached());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setBeached(nbt.getBoolean("Beached"));
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.2, x, z);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.catfish.swim", Animation.LoopType.LOOP));
        } else if (isBeached()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.catfish.beached", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.catfish.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    //SOUNDS
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SALMON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SALMON_DEATH;
    }
}
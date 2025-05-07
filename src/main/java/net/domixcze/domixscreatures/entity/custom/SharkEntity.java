package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ai.Beachable;
import net.domixcze.domixscreatures.entity.ai.BeachedGoal;
import net.domixcze.domixscreatures.entity.ai.SharkMeleeAttackGoal;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SharkEntity extends WaterCreatureEntity implements GeoEntity, Beachable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> BEACHED = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public SharkEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 70, 5, 0.01F, 0.05F, true);
        this.lookControl = new YawAdjustingLookControl(this, 3);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BeachedGoal(this, this));
        this.goalSelector.add(1, new SharkMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new SwimAroundGoal(this, 0.8, 12));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, (new RevengeGoal(this)));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BEACHED, false);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.2, x, z);
    }

    public boolean isBeached() {
        return this.dataTracker.get(BEACHED);
    }

    public void setBeached(boolean beached) {
        this.dataTracker.set(BEACHED, beached);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            if (!hasFullBleedingProtection(livingTarget)) {
                livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 200, 0)); // 200 ticks = 10 seconds
            }
        }
        return success;
    }

    private boolean hasFullBleedingProtection(LivingEntity entity) {
        return preventsBleeding(entity.getEquippedStack(EquipmentSlot.HEAD)) &&
                preventsBleeding(entity.getEquippedStack(EquipmentSlot.CHEST)) &&
                preventsBleeding(entity.getEquippedStack(EquipmentSlot.LEGS)) &&
                preventsBleeding(entity.getEquippedStack(EquipmentSlot.FEET));
    }

    private boolean preventsBleeding(ItemStack stack) {
        return stack.isIn(ModTags.Items.PREVENTS_BLEEDING);
    }

    /*protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }*/

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "water_controller",5 , this::waterPredicate));
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> State) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9 && this.isTouchingWater()) {
            State.getController().setAnimation(RawAnimation.begin().then("animation.shark.swim", Animation.LoopType.LOOP));
        } else if (isBeached()){
            State.getController().setAnimation(RawAnimation.begin().then("animation.shark.beached", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            State.getController().setAnimation(RawAnimation.begin().then("animation.shark.idle_swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
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
}
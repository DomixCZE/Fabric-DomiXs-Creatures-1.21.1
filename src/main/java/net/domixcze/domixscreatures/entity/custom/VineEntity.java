package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class VineEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Boolean> IS_HOLDING_ENTITY = DataTracker.registerData(VineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private int vineGrabCooldown = 0;

    public VineEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new GrabEntitiesGoal(this));
        this.goalSelector.add(1, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_HOLDING_ENTITY, false);
    }

    public boolean isHoldingEntity() {
        return this.dataTracker.get(IS_HOLDING_ENTITY);
    }

    public void setHoldingEntity(boolean holding) {
        this.dataTracker.set(IS_HOLDING_ENTITY, holding);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("VineGrabCooldown", vineGrabCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        vineGrabCooldown = nbt.getInt("VineGrabCooldown");
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.0, x, z);
    }

    public boolean isPushedByFluids() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (vineGrabCooldown > 0) {
            vineGrabCooldown--;
        }
    }

    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
        double xOffset = 0.0; //backward
        double yOffset = - 0.1; //up and down
        double zOffset = 0.0; // forward
        Vec3d offsetVector = (new Vec3d(xOffset, yOffset, zOffset)).rotateY(-this.bodyYaw * 0.017453292F);
        positionUpdater.accept(passenger, this.getX() + offsetVector.x, this.getY() + offsetVector.y, this.getZ() + offsetVector.z);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == this.getDamageSources().magic()) {
            return false;
        }

        if (source.isIn(DamageTypeTags.IS_FIRE)) {
            amount *= 2.0F;
        }

        return super.damage(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isHoldingEntity()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.vine.attack", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.vine.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.VINE_HURT;
    }

    /*@Override
    protected SoundEvent getDeathSound() {
        return ModSounds.VINE_DEATH;
    }*/

    static class GrabEntitiesGoal extends Goal {
        private final VineEntity vine;
        private int grabTime = 0;
        private static final int MAX_GRAB_TIME = 200;
        private static final int DAMAGE_ON_GRAB = 8;
        private static final float DAMAGE_PER_TICK = 2.0f;

        GrabEntitiesGoal(VineEntity vine) {
            this.vine = vine;
        }

        @Override
        public boolean canStart() {
            if (this.vine.vineGrabCooldown > 0 || this.vine.hasPassengers()) {
                return false;
            }
            List<LivingEntity> nearbyPlayers = this.vine.getWorld().getEntitiesByClass(LivingEntity.class, this.vine.getBoundingBox().expand(1.0), e -> e instanceof PlayerEntity);
            for (LivingEntity entity : nearbyPlayers) {
                if (entity.hasVehicle()) {
                    continue;
                }
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            List<LivingEntity> nearbyPlayers = this.vine.getWorld().getEntitiesByClass(LivingEntity.class, this.vine.getBoundingBox().expand(2.0), e -> e instanceof PlayerEntity);
            if (!nearbyPlayers.isEmpty()) {
                LivingEntity player = nearbyPlayers.get(0);
                player.startRiding(this.vine, true);
                this.vine.setHoldingEntity(true);
                grabTime = 0;
                player.damage(this.vine.getDamageSources().mobAttack(this.vine), DAMAGE_ON_GRAB);
            }
        }

        @Override
        public void tick() {
            if (!this.vine.hasPassengers()) {
                this.vine.setHoldingEntity(false);
                this.vine.vineGrabCooldown = 200;
                return;
            }

            Entity passenger = this.vine.getFirstPassenger();
            if (passenger instanceof PlayerEntity player) {
                if (grabTime % 20 == 0) {
                    player.damage(this.vine.getDamageSources().mobAttack(this.vine), DAMAGE_PER_TICK);
                }
                grabTime++;
                if (grabTime >= MAX_GRAB_TIME) {
                    player.stopRiding();
                    this.vine.setHoldingEntity(false);
                    this.vine.vineGrabCooldown = 200;
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            return this.vine.hasPassengers();
        }

        @Override
        public void stop() {
            this.vine.setHoldingEntity(false);
            if (this.vine.vineGrabCooldown <= 0) {
                this.vine.vineGrabCooldown = 200;
            }
        }
    }
}
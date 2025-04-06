package net.domixcze.domixscreatures.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagmaBallEntity extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int lifeTime = 100;

    public MagmaBallEntity(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "moveController", 0, this::predicate));
    }

    private <P extends GeoEntity> PlayState predicate(AnimationState<P> state) {
         if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.magma_ball.roll", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void tick() {
        super.tick();

        // Apply gravity
        if (!this.isOnGround()) {
            this.addVelocity(0, -0.04, 0);
        }

        if (this.age == 1) {
            Vec3d direction = this.getRotationVector();
            double speed = 0.2;
            this.setVelocity(direction.multiply(speed));
        }

        this.move(MovementType.SELF, this.getVelocity());
        spawnLavaParticles();

        // Check for entity collision
        for (Entity entity : this.getWorld().getOtherEntities(this, this.getBoundingBox())) {
            if (entity instanceof FireSalamanderEntity) {
                continue; // ignore all salamanders
            }

            if (entity instanceof LivingEntity) {
                entity.setOnFireFor(5);
                spawnDiscardParticles();
                this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.15F, 1.0F);
                this.discard();
                return;
            }
        }

        // Despawn in water
        if (this.isTouchingWater()) {
            spawnDiscardParticles();
            this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.15F, 1.0F);
            this.discard();
            return;
        }

        // Despawn in lava
        if (this.isInLava()) {
            spawnDiscardParticles();
            this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.15F, 1.0F);
            this.discard();
            return;
        }

        // Despawn after timer runs out
        if (--lifeTime <= 0) {
            spawnDiscardParticles();
            this.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.15F, 1.0F);
            this.discard();
        }
    }

    private void spawnLavaParticles() {
        if (!this.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.spawnParticles(
                    ParticleTypes.LANDING_LAVA,
                    this.getX(), this.getY(), this.getZ(),
                    1,
                    0.3, 0.0, 0.3,
                    0.1
            );
        }
    }

    private void spawnDiscardParticles() {
        if (!this.getWorld().isClient) {
            ServerWorld serverWorld = (ServerWorld) this.getWorld();
            serverWorld.spawnParticles(
                    ParticleTypes.LAVA,
                    this.getX(), this.getY(), this.getZ(),
                    5,
                    0.3, 0.3, 0.3,
                    0.1
            );
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.BLOCK_LAVA_POP, 0.15F, 1.0F);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
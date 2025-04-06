package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ShamanEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int vineAbilityCooldown = 0;
    private int poisonAbilityCooldown = 0;

    private final Set<UUID> spawnedVines = new HashSet<>();

    public ShamanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return HostileEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new ShamanCombatGoal(this, 1.0));
        this.goalSelector.add(2, new LookAroundGoal(this));
        this.goalSelector.add(3, new PoisonCloudGoal(this));
        this.goalSelector.add(4, new VineTrapGoal(this));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("VineAbilityCooldown", vineAbilityCooldown);
        nbt.putInt("PoisonAbilityCooldown", poisonAbilityCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.vineAbilityCooldown = nbt.getInt("VineAbilityCooldown");
        this.poisonAbilityCooldown = nbt.getInt("PoisonAbilityCooldown");
    }

    public void checkDespawn() {
    }

    @Override
    public void tick() {
        super.tick();
        if (vineAbilityCooldown > 0) vineAbilityCooldown--;
        if (poisonAbilityCooldown > 0) poisonAbilityCooldown--;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        this.clearVines();
    }

    public void clearVines() {
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;
        for (UUID id : spawnedVines) {
            Entity vine = serverWorld.getEntity(id);
            if (vine != null) vine.discard();
        }
        spawnedVines.clear();
    }

    public boolean areVinesAlive() {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return false;

        for (UUID vineId : spawnedVines) {
            Entity vine = serverWorld.getEntity(vineId);
            if (vine != null && vine.isAlive()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == this.getDamageSources().magic()) {
            return false;
        }

        if (areVinesAlive()) {
            this.getWorld().playSound(null, this.getBlockPos(), ModSounds.VINE_HURT, SoundCategory.HOSTILE, 1.0f, 0.8f + random.nextFloat() * 0.4f);

            return false;
        }

        return super.damage(source, amount);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.shaman.walk", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.shaman.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_EVOKER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_EVOKER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_EVOKER_DEATH;
    }

    public class PoisonCloudGoal extends Goal {
        final ShamanEntity shaman;

        PoisonCloudGoal(ShamanEntity shaman) { this.shaman = shaman; }

        @Override public boolean canStart() {
            return this.shaman.poisonAbilityCooldown == 0 && getTarget() != null;
        }

        @Override public void start() {
            AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(getWorld(), getX(), getY(), getZ());
            cloud.setPotion(Potions.POISON);
            cloud.setDuration(200);
            cloud.setRadius(3f);
            getWorld().spawnEntity(cloud);

            shaman.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            shaman.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 1));

            shaman.getWorld().playSound(null, shaman.getBlockPos(), SoundEvents.ENTITY_EVOKER_PREPARE_WOLOLO, SoundCategory.HOSTILE, 1.0f, 1.0f);

            this.shaman.poisonAbilityCooldown = 400;
        }
    }

    public class VineTrapGoal extends Goal {
        private final ShamanEntity shaman;

        VineTrapGoal(ShamanEntity shaman) { this.shaman = shaman; }

        @Override
        public boolean canStart() {
            return this.shaman.vineAbilityCooldown == 0 && getTarget() != null;
        }

        @Override
        public void start() {
            if (!(this.shaman.getWorld() instanceof ServerWorld serverWorld)) return;

            BlockPos shamanPos = this.shaman.getBlockPos();
            Set<BlockPos> chosenPositions = new HashSet<>();

            for (int i = 0; i < 2; i++) {
                BlockPos vinePos = getRandomSpreadPosition(shamanPos, chosenPositions);
                if (vinePos == null) continue;

                VineEntity vine = ModEntities.VINE.create(serverWorld);
                if (vine != null) {
                    vine.refreshPositionAndAngles(vinePos.getX() + 0.5, vinePos.getY() + 1, vinePos.getZ() + 0.5, 0, 0);
                    serverWorld.spawnEntity(vine);
                    this.shaman.spawnedVines.add(vine.getUuid());
                }
            }
            this.shaman.vineAbilityCooldown = 400;
        }

        private BlockPos getRandomSpreadPosition(BlockPos center, Set<BlockPos> alreadyChosen) {
            for (int attempt = 0; attempt < 10; attempt++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double distance = 4.0 + random.nextDouble() * 2.0;

                int offsetX = (int) Math.round(Math.cos(angle) * distance);
                int offsetZ = (int) Math.round(Math.sin(angle) * distance);

                BlockPos candidate = center.add(offsetX, 0, offsetZ);

                if (isPositionClear(candidate)) {
                    boolean tooClose = alreadyChosen.stream().anyMatch(existing -> existing.isWithinDistance(candidate, 3.0));
                    if (!tooClose) {
                        alreadyChosen.add(candidate);
                        return candidate;
                    }
                }
            }
            return null;
        }

        private boolean isPositionClear(BlockPos position) {
            return !getWorld().getBlockState(position).isSolid();
        }
    }

    public static class ShamanCombatGoal extends Goal {
        private final ShamanEntity shaman;
        private LivingEntity target;
        private final double speed;
        private int repositionCooldown;

        public ShamanCombatGoal(ShamanEntity shaman, double speed) {
            this.shaman = shaman;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            this.target = this.shaman.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            repositionCooldown = 0;
        }

        @Override
        public void tick() {
            if (target == null) return;

            double distance = shaman.distanceTo(target);
            shaman.getLookControl().lookAt(target, 30.0F, 30.0F);

            if (repositionCooldown-- > 0) return;
            repositionCooldown = 40; // Reposition every 2 seconds (40 ticks)

            if (distance < 4.0) {
                // Too close, move away
                moveAwayFromTarget();
            } else if (distance > 8.0) {
                // Too far, move closer
                moveTowardsTarget();
            }
        }

        private void moveAwayFromTarget() {
            Vec3d dir = shaman.getPos().subtract(target.getPos()).normalize().multiply(3.0);
            shaman.getNavigation().startMovingTo(shaman.getX() + dir.x, shaman.getY(), shaman.getZ() + dir.z, speed);
        }

        private void moveTowardsTarget() {
            shaman.getNavigation().startMovingTo(target.getX(), target.getY(), target.getZ(), speed);
        }
    }
}
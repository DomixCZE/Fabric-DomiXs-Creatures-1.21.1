package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.client.iguana.IguanaVariants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class IguanaEntity  extends TameableEntity implements GeoEntity, Sleepy {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(IguanaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(IguanaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(IguanaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public IguanaEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, false, true, true, false, 3.0, 500, 700, true, false, true, true));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, IguanaVariants.GREEN.getId());
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(SLEEPING, false);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
        if (sleeping) {
            this.getNavigation().stop();
            if (this.getOwner() instanceof PlayerEntity player && !this.isSitting()) {
                this.setSit(player, true);
            }
        }
    }

    public IguanaVariants getVariant() {
        return IguanaVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(IguanaVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSit(PlayerEntity player, boolean sitting) {
        if (player == getOwner()) {
            this.dataTracker.set(SITTING, sitting);
            super.setSitting(sitting);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.hasVehicle()) {
            return false;
        }
        return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        double randomValue = world.getRandom().nextDouble();

        if (randomValue < 0.01) {
            this.setVariant(IguanaVariants.ALBINO);
        } else if (randomValue < 0.06) {
            this.setVariant(IguanaVariants.MELANISTIC);
        } else if (randomValue < 0.31) {
            this.setVariant(IguanaVariants.BLUE);
        } else {
            this.setVariant(IguanaVariants.GREEN);
        }
        return entityData;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        if (itemstack.getItem() == Items.JUNGLE_LEAVES && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
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

        if (isTamed() && !this.getWorld().isClient()) {
            if (itemstack.getItem() == Items.STICK && hand == Hand.MAIN_HAND) {
                setSit(player, !isSitting());

                Text entityName = this.getDisplayName();
                String action = isSitting() ? "is Sitting" : "is Following";

                Text message = Text.literal(entityName.getString() + " " + action + ".")
                        .styled(style -> style.withColor(Formatting.GREEN));
                player.sendMessage(message, true);

                return ActionResult.PASS;
            }
        }

        if (isTamed() && !this.isBaby() && hand == Hand.MAIN_HAND && itemstack.isEmpty()) {
            if (!this.hasVehicle()) {
                if (player.getShoulderEntityLeft().isEmpty()) {
                    if (!isPlayerBeingRiddenByIguana(player)) {
                        this.startRiding(player, true);
                        return ActionResult.SUCCESS;
                    } else {
                        return ActionResult.PASS;
                    }
                } else {
                    return ActionResult.PASS;
                }
            }
        }
        return super.interactMob(player, hand);
    }

    private boolean isPlayerBeingRiddenByIguana(PlayerEntity player) {
        for (Entity passenger : player.getPassengerList()) {
            if (passenger instanceof IguanaEntity) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isSleeping()) {
            this.getNavigation().stop();
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();

        if (this.hasVehicle()) {
            Entity vehicle = this.getVehicle();

            if (vehicle instanceof PlayerEntity player) {
                float bodyYaw = player.bodyYaw;
                this.setHeadYaw(bodyYaw);
                this.setBodyYaw(bodyYaw);
                // Dismount if the player is underwater (the player has to be almost fully submerged in water for the entity to dismount)
                if (player.isSubmergedInWater()) {
                    this.stopRiding();
                }
                // Dismount if a parrot or any entity using the vanilla shoulder mount system mounts the player
                if (!player.getShoulderEntityLeft().isEmpty()) {
                    this.stopRiding();
                }

                //Dismount if the player is Sneaking on Ground (you can use shift while flying in creative without the entity dismounting)
                if (player.isSneaking() && player.isOnGround()) {
                    this.stopRiding();
                }

                // Dismount if the player is Flying with Elytra
                if (player.isFallFlying()) {
                    this.stopRiding();
                }

                //Dismount if the player is Crawling
                if (player.isCrawling()) {
                    this.stopRiding();
                }

                float yaw = bodyYaw;
                float offsetX = 0.4f; // Adjust left/right position
                float offsetY = 0.0f; // Adjust vertical position
                float offsetZ = - 0.1f; // Adjust forward/backward position
                yaw = (float) Math.toRadians(yaw);

                double newX = player.getX() + offsetX * Math.cos(yaw) - offsetZ * Math.sin(yaw);
                double newZ = player.getZ() + offsetZ * Math.cos(yaw) + offsetX * Math.sin(yaw);
                double newY = player.getY() + player.getMountedHeightOffset() + offsetY;

                this.setPosition(newX, newY, newZ);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("Variant", this.getVariant().getId());
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setVariant(IguanaVariants.byId(nbt.getInt("Variant")));
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.APPLE);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "shoulder_controller", 5, this::shoulderPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (state.isMoving()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_iguana.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.walk", Animation.LoopType.LOOP));
            }
        } else if (this.isSitting() && !this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_iguana.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.sit", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_iguana.idle", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState shoulderPredicate(AnimationState<T> state) {
        if (this.hasVehicle()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.shoulder", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_iguana.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.sleep", Animation.LoopType.LOOP));
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
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_iguana.swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.iguana.swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        IguanaEntity baby = (IguanaEntity) ModEntities.IGUANA.create(world);
        if (baby != null) {
            IguanaEntity parent1 = this;
            IguanaEntity parent2 = (IguanaEntity) entity;

            if (parent2 != null) {
                if (world.getRandom().nextBoolean()) {
                    baby.setVariant(parent1.getVariant());
                } else {
                    baby.setVariant(parent2.getVariant());
                }
            } else {
                double randomValue = world.getRandom().nextDouble();

                if (randomValue < 0.01) {
                    baby.setVariant(IguanaVariants.ALBINO);
                } else if (randomValue < 0.06) {
                    baby.setVariant(IguanaVariants.MELANISTIC);
                } else if (randomValue < 0.31) {
                    baby.setVariant(IguanaVariants.BLUE);
                } else {
                    baby.setVariant(IguanaVariants.GREEN);
                }
            }
        }
        return baby;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.WispAttackGoal;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class WispEntity extends TameableEntity implements GeoEntity {

    private static final TrackedData<Boolean> IS_WEARING_SKULL = DataTracker.registerData(WispEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(WispEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public WispEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setIsWearingSkull(true);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(2, new WispAttackGoal(this, 1.0, false, 1.0f));
        this.goalSelector.add(2, new FlyGoal(this, 1.0));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.1F)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.WISP.create(world);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    public static boolean canSpawn(EntityType<WispEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return random.nextInt(20) == 0 && canMobSpawn(type, world, spawnReason, pos, random);
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return !this.hasCustomName() && !this.isTamed();
    }

    @Override
    public int getLimitPerChunk() {
        return 2;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> wispAnimationState) {
        if (wispAnimationState.isMoving()) {
            wispAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wisp.walk", Animation.LoopType.LOOP));
        } else {
            wispAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wisp.idle", Animation.LoopType.LOOP));

        }
        return PlayState.CONTINUE;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            return;
        }

        if (this.random.nextFloat() < 0.1f) {
            double particleX = this.getX() + (this.random.nextDouble() - 0.5) * 0.5;
            double particleY = this.getY() + (this.random.nextDouble() - 0.5) * 0.5 + 0.5;
            double particleZ = this.getZ() + (this.random.nextDouble() - 0.5) * 0.5;

            this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, particleX, particleY, particleZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        //Remove skull with shift right click
        if (this.isTamed() && this.isWearingSkull() && player.isSneaking()) {
            this.setIsWearingSkull(false);
            this.dropSkull();
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }
        //Tame with bones
        if (!this.isTamed() && !this.isWearingSkull() && itemStack.getItem() == Items.BONE) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            if (this.random.nextInt(3) == 0) {
                super.setOwner(player);
                this.navigation.recalculatePath();
                this.setTarget(null);
                this.getWorld().sendEntityStatus(this, (byte)7);
            }

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 1.0F, 1.0F);

            return ActionResult.SUCCESS;
        }
        //Equip skull
        else if (this.isTamed() && itemStack.getItem() == ModItems.SKULL && !this.isWearingSkull()) {
            this.setIsWearingSkull(true);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        }
        //Sitting using skull wand
        if (this.isTamed() && !this.getWorld().isClient()) {
            if (itemStack.getItem() == ModItems.SKULL_WAND && hand == Hand.MAIN_HAND) {
                this.setSit(player, !isSitting());

                Text entityName = this.getDisplayName();
                String action = isSitting() ? "is Sitting" : "is Following";

                Text message = Text.literal(entityName.getString() + " " + action + ".")
                        .styled(style -> style.withColor(Formatting.GREEN));
                player.sendMessage(message, true);

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof PlayerEntity && this.isWearingSkull()) {
            this.dropSkull();
            this.setIsWearingSkull(false);
        }
        return super.damage(source, amount);
    }

    private void dropSkull() {
        ItemStack skullItem = new ItemStack(ModItems.SKULL);
        this.dropStack(skullItem);
    }

    public void setIsWearingSkull(boolean isWearingSkull) {
        if (this.isWearingSkull() != isWearingSkull) {
            this.dataTracker.set(IS_WEARING_SKULL, isWearingSkull);
        }
    }

    public boolean isWearingSkull() {
        return this.dataTracker.get(IS_WEARING_SKULL);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("IsWearingSkull", this.isWearingSkull());
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
        this.setIsWearingSkull(nbt.getBoolean("IsWearingSkull"));
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_WEARING_SKULL, true);
        this.dataTracker.startTracking(SITTING, false);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(true);
        birdNavigation.setCanEnterOpenDoors(true);
        return birdNavigation;
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.isSitting()) {
            return;
        }

        if (this.isLogicalSideForUpdatingMovement()) {
            if (this.isTouchingWater()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.8F));

            } else if (this.isInLava()) {
                this.updateVelocity(0.02F, movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.5));

            } else {
                this.updateVelocity(this.getMovementSpeed(), movementInput);
                this.move(MovementType.SELF, this.getVelocity());
                this.setVelocity(this.getVelocity().multiply(0.91F));
            }
        }
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ALLAY_DEATH;
    }
}

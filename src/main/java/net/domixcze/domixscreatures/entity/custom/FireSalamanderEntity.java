package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.FireSalamanderMagmaBallAttackGoal;
import net.domixcze.domixscreatures.entity.ai.FireSalamanderMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.client.fire_salamander.FireSalamanderVariants;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class FireSalamanderEntity extends TameableEntity implements GeoEntity {

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(FireSalamanderEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(FireSalamanderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_CHARGING = DataTracker.registerData(FireSalamanderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int magmaBallChargeTime = 0;
    private int magmaBallCooldown = 0;
    private static final int SMELTING_COOLDOWN = 300;
    private int smeltingTimer = 0;
    private ItemStack smeltingItem = ItemStack.EMPTY;

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public FireSalamanderEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
        this.setPathfindingPenalty(PathNodeType.WATER_BORDER, -1.0F);
        this.lookControl = new SalamanderLookControl(this);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    class SalamanderLookControl extends LookControl {
        public SalamanderLookControl(MobEntity entity) {
            super(entity);
        }

        @Override
        public void tick() {
            if (!isCharging()) {
                super.tick();
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new FireSalamanderMagmaBallAttackGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(2, new FireSalamanderMeleeAttackGoal(this, 1.0, false, 2.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.FIRE_SALAMANDER.create(world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        this.setVariant(FireSalamanderVariants.MAGMA);

        return entityData;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, FireSalamanderVariants.MAGMA.getId());
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(IS_CHARGING, false);
    }

    public FireSalamanderVariants getVariant() {
        return FireSalamanderVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(FireSalamanderVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public boolean isObsidianVariant() {
        return this.getVariant() == FireSalamanderVariants.OBSIDIAN;
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

    public int getMagmaBallCooldown() {
        return magmaBallCooldown;
    }

    public void setMagmaBallCooldown(int cooldown) {
        this.magmaBallCooldown = cooldown;
    }

    public int getMagmaBallChargeTime() {
        return this.magmaBallChargeTime;
    }

    public void setMagmaBallChargeTime(int time) {
        this.magmaBallChargeTime = time;
    }

    public boolean isCharging() {
        return this.dataTracker.get(IS_CHARGING);
    }

    public void setCharging(boolean charging) {
        this.dataTracker.set(IS_CHARGING, charging);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("MagmaBallChargeTime", magmaBallChargeTime);
        nbt.putInt("MagmaBallCooldown", magmaBallCooldown);
        nbt.putInt("Variant", this.getVariant().getId());
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putBoolean("isCharging", this.dataTracker.get(IS_CHARGING));

        if (!this.smeltingItem.isEmpty()) {
            NbtCompound itemNbt = new NbtCompound();
            this.smeltingItem.writeNbt(itemNbt);
            nbt.put("SmeltingItem", itemNbt);
        }
        nbt.putInt("SmeltingTimer", this.smeltingTimer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        magmaBallChargeTime = nbt.getInt("MagmaBallChargeTime");
        magmaBallCooldown = nbt.getInt("MagmaBallCooldown");
        this.setVariant(FireSalamanderVariants.byId(nbt.getInt("Variant")));
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.dataTracker.set(IS_CHARGING, nbt.getBoolean("isCharging"));

        if (nbt.contains("SmeltingItem")) {
            this.smeltingItem = ItemStack.fromNbt(nbt.getCompound("SmeltingItem"));
        } else {
            this.smeltingItem = ItemStack.EMPTY;
        }
        this.smeltingTimer = nbt.getInt("SmeltingTimer");
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (this.isInLava()) {
                movementSpeed.setBaseValue(1.5f); // Fast speed in lava
            } else {
                movementSpeed.setBaseValue(0.2f); // Slow speed on land
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "lava_controller", 5, this::lavaPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.walk", Animation.LoopType.LOOP));
        } else if (this.isSitting()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.sit", Animation.LoopType.LOOP));
        } else if (this.isCharging()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.charge", Animation.LoopType.PLAY_ONCE));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState lavaPredicate(AnimationState<T> state) {
        if (!this.isInLava()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.fire_salamander.idle_swim", Animation.LoopType.LOOP));
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
        if (magmaBallCooldown > 0) {
            magmaBallCooldown--;
        }

        if (this.isWet() && !this.isObsidianVariant()) {
            this.setVariant(FireSalamanderVariants.OBSIDIAN);
        }

        if (!this.smeltingItem.isEmpty()) {
            if (this.smeltingTimer > 0) {
                this.smeltingTimer--;
            } else {
                smeltItem();
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        if (itemstack.getItem() == Items.MAGMA_CREAM && !isTamed() && isObsidianVariant()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                if (!this.getWorld().isClient()) {
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
            if (itemstack.getItem() == Items.BLAZE_ROD && hand == Hand.MAIN_HAND) {
                setSit(player, !isSitting());

                Text entityName = this.getDisplayName();
                String action = isSitting() ? "is Sitting" : "is Following";

                Text message = Text.literal(entityName.getString() + " " + action + ".")
                        .styled(style -> style.withColor(Formatting.GREEN));
                player.sendMessage(message, true);

                return ActionResult.PASS;
            }

            if (canSmelt(itemstack) && smeltingItem.isEmpty()) {
                startSmelting(itemstack);
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    private boolean canSmelt(ItemStack stack) {
        return this.getWorld().getRecipeManager().getFirstMatch(RecipeType.SMELTING, new net.minecraft.inventory.SimpleInventory(stack), this.getWorld()).isPresent();
    }

    private void startSmelting(ItemStack stack) {
        this.smeltingItem = stack.copy();
        this.smeltingTimer = SMELTING_COOLDOWN;
    }

    private void smeltItem() {
        if (!this.smeltingItem.isEmpty() && this.getWorld() instanceof ServerWorld serverWorld) {
            Optional<SmeltingRecipe> recipe = serverWorld.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new net.minecraft.inventory.SimpleInventory(this.smeltingItem), serverWorld);
            if (recipe.isPresent()) {
                ItemStack result = recipe.get().getOutput(serverWorld.getRegistryManager()).copy();
                this.dropStack(result);
            }
            this.smeltingItem = ItemStack.EMPTY;
        }
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return true;
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.FIRE_SALAMANDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.FIRE_SALAMANDER_DEATH;
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class RaccoonEntity extends TameableEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_WASHING = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> HELD_FOOD = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final int STEAL_COOLDOWN_TICKS = 2400;

    private int washingTicks = 0;
    private int stealCooldown = 0;
    private int marshmallowsEaten = 0;

    public RaccoonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,true, false, true, false, 7.0, 600, 800, false, false, true, true, 1));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, WolfEntity.class, 10.0f,1.0,1.0));
        this.goalSelector.add(2, new StealFoodGoal(this, 8));
        this.goalSelector.add(3, new WashFoodGoal(this, 8));
        this.goalSelector.add(3, new BabyFollowParentGoal(this, 1.25));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(5, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(IS_WASHING, false);
        builder.add(HELD_FOOD, ItemStack.EMPTY);
        builder.add(SLEEPING, false);
        builder.add(SITTING, false);
    }

    @Override
    public boolean hasSnowLayer() {
        return this.dataTracker.get(HAS_SNOW_LAYER);
    }

    @Override
    public void setHasSnowLayer(boolean hasSnow) {
        this.dataTracker.set(HAS_SNOW_LAYER, hasSnow);
    }

    @Override
    public int getSnowTicks() {
        return this.snowTicks;
    }

    @Override
    public void setSnowTicks(int ticks) {
        this.snowTicks = ticks;
    }

    @Override
    public int getSnowMeltTimer() {
        return this.snowMeltTimer;
    }

    @Override
    public void setSnowMeltTimer(int timer) {
        this.snowMeltTimer = timer;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putInt("MarshmallowsEaten", marshmallowsEaten);
        nbt.putBoolean("IsWashing", this.dataTracker.get(IS_WASHING));
        nbt.putInt("WashingTicks", washingTicks);
        nbt.putInt("StealCooldown", this.stealCooldown);
        if (!this.getHeldFood().isEmpty()) {
            nbt.put("HeldFood", ItemStack.CODEC.encodeStart(this.getRegistryManager().getOps(NbtOps.INSTANCE), this.getHeldFood())
                    .resultOrPartial(error -> {
                    }).orElse(new NbtCompound()));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        if (nbt.contains("MarshmallowsEaten")) {
            marshmallowsEaten = nbt.getInt("MarshmallowsEaten");
        }
        if (nbt.contains("IsWashing")) {
            this.dataTracker.set(IS_WASHING, nbt.getBoolean("IsWashing"));
        }
        if (nbt.contains("WashingTicks")) {
            this.washingTicks = nbt.getInt("WashingTicks");
        }
        if (nbt.contains("StealCooldown")) {
            this.stealCooldown = nbt.getInt("StealCooldown");
        }
        if (nbt.contains("HeldFood")) {
            ItemStack heldItem = ItemStack.CODEC.parse(this.getRegistryManager().getOps(NbtOps.INSTANCE), nbt.get("HeldFood"))
                    .resultOrPartial(error -> {
                    }).orElse(ItemStack.EMPTY);
            this.setHeldFood(heldItem);
        } else {
            this.setHeldFood(ItemStack.EMPTY);
        }
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        super.dropInventory();
        ItemStack heldItem = this.getHeldFood();
        if (!heldItem.isEmpty()) {
            this.dropStack(heldItem);
            this.setHeldFood(ItemStack.EMPTY);
        }
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.5F, 0.4F);
        }
        return this.getType().getDimensions();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.RACCOON_FOR_BREEDING);
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

    public boolean hasFood() {
        return !this.getHeldFood().isEmpty();
    }

    public ItemStack getHeldFood() {
        return this.dataTracker.get(HELD_FOOD);
    }

    public void setHeldFood(ItemStack stack) {
        this.dataTracker.set(HELD_FOOD, stack);
    }

    public boolean isFood(ItemStack stack) {
        return stack.isIn(ModTags.Items.RACCOON_STEALS);
    }

    public boolean isWashing() {
        return this.dataTracker.get(IS_WASHING);
    }

    public void startWashing() {
        this.dataTracker.set(IS_WASHING, true);
        washingTicks = 200;
    }

    public void stopWashing() {
        this.dataTracker.set(IS_WASHING, false);
        washingTicks = 0;
    }

    public int getStealCooldown() {
        return this.stealCooldown;
    }

    public void resetStealCooldown() {
        this.stealCooldown = STEAL_COOLDOWN_TICKS;
    }

    @Override
    public void tick() {
        super.tick();

        SnowLayerUtil.handleSnowLayerTick(this, this);

        if (this.stealCooldown > 0) {
            this.stealCooldown--;
        }

        if (isWashing()) {
            washingTicks--;
            if (washingTicks <= 0 && !getWorld().isClient) {
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);

                ItemStack heldItem = this.getHeldFood();

                if (!heldItem.isEmpty()) {
                    boolean wasMarshmallow = heldItem.isOf(ModItems.MARSHMALLOW);

                    heldItem.decrement(1);
                    this.setHeldFood(heldItem.isEmpty() ? ItemStack.EMPTY : heldItem);

                    if (wasMarshmallow && !this.isTamed()) {
                        marshmallowsEaten++;
                        if (marshmallowsEaten >= 3 && !this.isTamed()) {
                            this.tameNearestPlayer();
                        }
                    }
                }

                stopWashing();
            }
        }
    }

    private void tameNearestPlayer() {
        if (this.getWorld().isClient) return;

        PlayerEntity nearest = this.getWorld().getClosestPlayer(this, 8.0);
        if (nearest != null) {
            this.setOwnerUuid(nearest.getUuid());
            this.setTamed(true,false);
            this.getWorld().sendEntityStatus(this, (byte)7);
        }
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (isTamed() && !this.getWorld().isClient()) {
            if (isTamed() && hand == Hand.MAIN_HAND && itemStack.isEmpty()) {
                setSit(player, !isSitting());

                Text entityName = this.getDisplayName();
                Text action = Text.translatable(isSitting()
                        ? "message.domixs-creatures.action.sitting"
                        : "message.domixs-creatures.action.following");

                Text message = Text.literal(entityName.getString() + " ")
                        .append(action)
                        .append(".")
                        .styled(style -> style.withColor(Formatting.GREEN));
                player.sendMessage(message, true);

                return ActionResult.PASS;
            }
        }

        if (!ModConfig.INSTANCE.enableRaccoonStealing && !this.isTamed() && itemStack.isOf(ModItems.MARSHMALLOW)) {
            if (!player.getWorld().isClient) {
                itemStack.decrement(1);
                this.marshmallowsEaten++;
                if (this.marshmallowsEaten >= 3) {
                    this.setOwnerUuid(player.getUuid());
                    this.setTamed(true, false);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                }
            }
            return ActionResult.SUCCESS;
        }

        if (itemStack.isOf(Items.BRUSH) && this.hasSnowLayer()) {
            if (!this.getWorld().isClient) {
                this.setHasSnowLayer(false);
                this.snowMeltTimer = 0;

                int count = 3 + this.getWorld().random.nextInt(2);
                this.dropStack(new ItemStack(Items.SNOWBALL, count));
            }

            SnowLayerUtil.spawnSnowParticles(this);

            this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0F, 1.0F);
            if (!player.isCreative()) {
                itemStack.damage(1, player, EquipmentSlot.MAINHAND);
            }

            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.RACCOON.create(world);
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }

        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_raccoon.walk", Animation.LoopType.LOOP));
            } else if (this.isSitting() && !this.isSleeping()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_raccoon.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_raccoon.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isWashing()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.washing", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.walk", Animation.LoopType.LOOP));
            } else if (this.isSitting() && !this.isSleeping()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.idle", Animation.LoopType.LOOP));
            }
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_raccoon.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_raccoon.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.raccoon.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return ModSounds.RACCOON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.RACCOON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.RACCOON_DEATH;
    }

    public static class StealFoodGoal extends Goal {
        private final RaccoonEntity raccoon;
        private final double range;
        private BlockPos targetChestPos;

        public StealFoodGoal(RaccoonEntity raccoon, double range) {
            this.raccoon = raccoon;
            this.range = range;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!ModConfig.INSTANCE.enableRaccoonStealing) {
                return false;
            }

            if (raccoon.isSleeping() || raccoon.isBaby() || raccoon.isTamed() || raccoon.hasFood() || raccoon.getStealCooldown() > 0) {
                return false;
            }

            List<WolfEntity> wolves = raccoon.getWorld().getEntitiesByClass(
                    WolfEntity.class,
                    raccoon.getBoundingBox().expand(10.0D),
                    wolf -> wolf.isAlive() && !wolf.isBaby()
            );

            if (!wolves.isEmpty()) {
                return false;
            }

            World world = raccoon.getWorld();
            BlockPos origin = raccoon.getBlockPos();

            for (BlockPos pos : BlockPos.iterateOutwards(origin, (int) range, (int) range, (int) range)) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity) {
                    Inventory inv = (Inventory) be;
                    for (int i = 0; i < inv.size(); i++) {
                        if (raccoon.isFood(inv.getStack(i))) {
                            targetChestPos = pos;
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            if (targetChestPos != null && !raccoon.hasFood()) {
                BlockEntity be = raccoon.getWorld().getBlockEntity(targetChestPos);
                if (be instanceof Inventory inv) {
                    for (int i = 0; i < inv.size(); i++) {
                        if (raccoon.isFood(inv.getStack(i))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public void tick() {
            if (raccoon.squaredDistanceTo(targetChestPos.getX() + 0.5, targetChestPos.getY(), targetChestPos.getZ() + 0.5) < 2.0) {
                BlockEntity be = raccoon.getWorld().getBlockEntity(targetChestPos);
                if (be instanceof Inventory inv) {
                    for (int i = 0; i < inv.size(); i++) {
                        ItemStack stack = inv.getStack(i);
                        if (!stack.isEmpty() && raccoon.isFood(stack)) {
                            ItemStack stolen = stack.split(1);
                            raccoon.setHeldFood(stolen);
                            raccoon.getNavigation().stop();
                            be.markDirty();

                            break;
                        }
                    }
                    if (!raccoon.hasFood()) {
                        this.stop();
                    }
                }
            } else {
                raccoon.getNavigation().startMovingTo(
                        targetChestPos.getX() + 0.5,
                        targetChestPos.getY(),
                        targetChestPos.getZ() + 0.5,
                        1.0);
            }
        }

        @Override
        public void stop() {
            if (raccoon.hasFood()) {
                raccoon.resetStealCooldown();
            }
            targetChestPos = null;
        }
    }

    public static class WashFoodGoal extends Goal {
        private final RaccoonEntity raccoon;
        private final double range;
        private BlockPos waterPos;
        private BlockPos targetPos;
        private int soundPlayCooldown = 0;

        private final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

        public WashFoodGoal(RaccoonEntity raccoon, double range) {
            this.raccoon = raccoon;
            this.range = range;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (raccoon.isSleeping() || raccoon.isBaby() || !raccoon.hasFood() || this.raccoon.isInFluid()) {
                return false;
            }

            waterPos = getTarget();
            if (waterPos != null) {
                targetPos = getLandPos(waterPos);
                return targetPos != null;
            }

            return false;
        }

        @Override
        public boolean shouldContinue() {
            return targetPos != null && raccoon.hasFood() && !this.raccoon.isInFluid();
        }

        @Override
        public void tick() {
            if (waterPos != null) {

                if (!this.raccoon.getWorld().getFluidState(waterPos).isIn(FluidTags.WATER)) {
                    this.stop();
                    return;
                }

                double dist = this.raccoon.squaredDistanceTo(Vec3d.ofCenter(waterPos));
                soundPlayCooldown--;

                if (dist > 2 && this.raccoon.isWashing()) {
                    this.raccoon.stopWashing();
                }

                if (dist <= 1.0D) {
                    this.raccoon.getNavigation().stop();

                    double d0 = waterPos.getX() + 0.5D - this.raccoon.getX();
                    double d2 = waterPos.getZ() + 0.5D - this.raccoon.getZ();
                    float yaw = (float)(MathHelper.atan2(d2, d0) * (double)MathHelper.DEGREES_PER_RADIAN) - 90.0F;
                    this.raccoon.setYaw(yaw);
                    this.raccoon.setHeadYaw(yaw);
                    this.raccoon.bodyYaw = yaw;

                    if (!this.raccoon.isWashing()) {
                        this.raccoon.startWashing();
                    }

                    if (soundPlayCooldown <= 0) {
                        this.raccoon.playSound(SoundEvents.ENTITY_GENERIC_SWIM, 0.7F, 0.5F + raccoon.getRandom().nextFloat());
                        soundPlayCooldown = 20; // Cooldown of 20 ticks (1 second)
                    }

                } else {
                    this.raccoon.getNavigation().startMovingTo(waterPos.getX(), waterPos.getY(), waterPos.getZ(), 1.2D);
                }
            }
        }

        @Override
        public void stop() {
            targetPos = null;
            waterPos = null;
            this.raccoon.stopWashing();
            this.raccoon.getNavigation().stop();
            soundPlayCooldown = 0;
        }

        public BlockPos getTarget() {
            World world = this.raccoon.getWorld();
            Random random = this.raccoon.getRandom();
            int range = (int) this.range;

            for (int i = 0; i < 15; i++) {
                BlockPos searchPos = this.raccoon.getBlockPos().add(random.nextInt(range) - range / 2, 3, random.nextInt(range) - range / 2);
                while (world.getBlockState(searchPos).isAir() && searchPos.getY() > world.getBottomY()) {
                    searchPos = searchPos.down();
                }
                if (isNextToLand(searchPos)) {
                    return searchPos;
                }
            }
            return null;
        }

        public boolean isNextToLand(BlockPos pos) {
            World world = this.raccoon.getWorld();
            if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
                for (Direction dir : HORIZONTALS) {
                    BlockPos offsetPos = pos.offset(dir);
                    if (world.getFluidState(offsetPos).isEmpty() && world.getBlockState(offsetPos.up()).isAir()) {
                        return true;
                    }
                }
            }
            return false;
        }

        public BlockPos getLandPos(BlockPos pos) {
            World world = this.raccoon.getWorld();
            if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
                for (Direction dir : HORIZONTALS) {
                    BlockPos offsetPos = pos.offset(dir);
                    if (world.getFluidState(offsetPos).isEmpty() && world.getBlockState(offsetPos.up()).isAir()) {
                        return offsetPos;
                    }
                }
            }
            return null;
        }
    }
}
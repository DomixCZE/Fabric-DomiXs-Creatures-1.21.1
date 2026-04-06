package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.KomodoDragonEggBlock;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.goal.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.goal.BaskGoal;
import net.domixcze.domixscreatures.entity.ai.goal.MoveToSunGoal;
import net.domixcze.domixscreatures.entity.ai.goal.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.goal.entity.KomodoDragonMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.goal.util.Baskable;
import net.domixcze.domixscreatures.entity.ai.goal.util.Sleepy;
import net.domixcze.domixscreatures.entity.ai.goal.util.SnowLayerable;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class KomodoDragonEntity extends TameableEntity implements GeoEntity, Sleepy, SnowLayerable, Saddleable, Baskable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;
    private int baskingCooldown = 0;
    private int venomCooldown;

    private static final TrackedData<Boolean> HAS_EGG = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> IS_BASKING = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_VENOM = DataTracker.registerData(KomodoDragonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.fixed(0.5F, 0.4F);
    private static final EntityDimensions ADULT_DIMENSIONS = EntityDimensions.fixed(1.0F, 0.8F);
    private static final EntityDimensions SLEEPING_BABY_DIMENSIONS = EntityDimensions.fixed(0.6F, 0.4F);
    private static final EntityDimensions SLEEPING_ADULT_DIMENSIONS = EntityDimensions.fixed(1.2F, 0.6F);

    public KomodoDragonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.venomCooldown = 8000 + this.random.nextInt(4000);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,false, true, true, false, 3.0, 500, 700, false, false, true, true,2));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new KomodoDragonMateGoal(this, 1.0));
        this.goalSelector.add(1, new KomodoDragonLayEggGoal(this, 1.0));
        this.goalSelector.add(1, new BaskGoal<>(this, 4000, 8000, 1200));
        this.goalSelector.add(1, new MoveToSunGoal<>(this, 12, 3, 4000, 8000));
        this.goalSelector.add(2, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(2, new KomodoDragonMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(0, new RevengeGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, DeerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, BoarEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PigEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, SheepEntity.class, true));
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 350, 1), this);
        }

        return success;
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.2, x, z);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_VENOM, false);
        builder.add(IS_BASKING, false);
        builder.add(HAS_EGG, false);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(SADDLED, false);
        builder.add(SLEEPING, false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SLEEPING.equals(data)) {
            this.calculateDimensions();
        }
        super.onTrackedDataSet(data);
    }

    public EntityDimensions getBaseDimensions(EntityPose pose) {
        return this.getCustomDimensions(pose);
    }

    private EntityDimensions getCustomDimensions(EntityPose pose) {
        if (this.isSleeping()) {
            return this.isBaby() ? SLEEPING_BABY_DIMENSIONS : SLEEPING_ADULT_DIMENSIONS;
        }
        return this.isBaby() ? BABY_DIMENSIONS : ADULT_DIMENSIONS;
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);

        if (sleeping) {
            this.getNavigation().stop();

            if (this.getOwner() instanceof PlayerEntity && !this.isSitting()) {
                this.setSitting(true);
            }
        }
    }

    @Override
    public boolean canBeSaddled() {
        if (!this.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_KOMODO_DRAGON_RIDING)) {
            return false;
        }
        return this.isAlive() && !this.isBaby() && this.isTamed();
    }

    @Override
    public void saddle(ItemStack stack, @Nullable SoundCategory sound) {
        this.dataTracker.set(SADDLED, true);
        if (sound != null) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), this.getSaddleSound(), sound, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isSaddled() {
        return this.dataTracker.get(SADDLED);
    }

    public void unsaddle() {
        this.dataTracker.set(SADDLED, false);
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

    public boolean hasVenom() {
        return this.dataTracker.get(HAS_VENOM);
    }

    public void setHasVenom(boolean value) {
        this.dataTracker.set(HAS_VENOM, value);
    }

    public boolean hasEgg() {
        return this.dataTracker.get(HAS_EGG);
    }

    void setHasEgg(boolean hasEgg) {
        this.dataTracker.set(HAS_EGG, hasEgg);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasVenom", this.hasVenom());
        nbt.putInt("VenomCooldown", this.venomCooldown);
        nbt.putBoolean("HasEgg", this.hasEgg());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("Saddled", this.isSaddled());
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("IsBasking", this.isBasking());
        nbt.putInt("BaskingCooldown", this.baskingCooldown);
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasVenom(nbt.getBoolean("HasVenom"));
        this.venomCooldown = nbt.getInt("VenomCooldown");
        this.setHasEgg(nbt.getBoolean("HasEgg"));
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.dataTracker.set(SADDLED, nbt.getBoolean("Saddled"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setBasking(nbt.getBoolean("IsBasking"));
        this.baskingCooldown = nbt.getInt("BaskingCooldown");
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.hasPassengers() && this.getControllingPassenger() instanceof PlayerEntity) {
            LivingEntity livingentity = this.getControllingPassenger();
            this.setYaw(livingentity.getYaw());
            this.prevYaw = this.getYaw();
            this.setPitch(livingentity.getPitch() * 0.5F);
            this.setRotation(this.getYaw(), this.getPitch());
            this.bodyYaw = this.getYaw();
            this.headYaw = this.bodyYaw;

            float forward = livingentity.forwardSpeed;
            float sideways = livingentity.sidewaysSpeed;
            float speedMultiplier = 0.4F;

            this.setMovementSpeed((float) this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * speedMultiplier);
            super.travel(new Vec3d(sideways * speedMultiplier, movementInput.y, forward * speedMultiplier));
        } else {
            super.travel(movementInput);
        }
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_KOMODO_DRAGON_RIDING)) {
            return null; // No one can control it if riding is disabled.
        }
        return this.isSaddled() ? (LivingEntity) this.getFirstPassenger() : null;
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
        Vec2f controlledRotation = this.getControlledRotation(controllingPlayer);
        this.setRotation(controlledRotation.y, controlledRotation.x);
        this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();

        Vec3d controlledMovement = this.getControlledMovementInput(controllingPlayer, movementInput);
        this.setMovementSpeed(this.getSaddledSpeed(controllingPlayer));

        travel(controlledMovement);
    }

    protected Vec2f getControlledRotation(LivingEntity controllingPassenger) {
        return new Vec2f(controllingPassenger.getPitch() * 0.5F, controllingPassenger.getYaw());
    }

    @Override
    public void tick() {
        super.tick();
        SnowLayerUtil.handleSnowLayerTick(this, this);

        if (!this.getWorld().isClient()) {

            if (!this.isBaby()) {

                if (!this.hasVenom()) {
                    if (this.venomCooldown > 0) {
                        this.venomCooldown--;
                    } else {
                        this.setHasVenom(true);
                    }
                }

            }
        }

        if (this.baskingCooldown > 0) {
            this.baskingCooldown--;
        }

        if (!this.getWorld().isClient()
                && this.hasPassengers()
                && !this.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_KOMODO_DRAGON_RIDING)) {

            Entity passenger = this.getFirstPassenger();
            if (passenger != null) {
                passenger.stopRiding();
            }
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.KOMODO_DRAGON_FOR_BREEDING);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        World world = this.getWorld();

        if (itemStack.isOf(Items.GLASS_BOTTLE) && this.hasVenom()) {

            if (!world.isClient) {

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                ItemStack venomBottle = new ItemStack(ModItems.KOMODO_DRAGON_VENOM_BOTTLE);
                if (!player.giveItemStack(venomBottle)) {
                    player.dropItem(venomBottle, false);
                }

                this.setHasVenom(false);
                this.venomCooldown = 8000 + this.random.nextInt(4000);

                if (!this.isTamed()) {
                    boolean shouldAggro = true;

                    if (player.isSneaking()) {
                        // 50% chance to avoid aggro
                        shouldAggro = this.random.nextBoolean();
                    }

                    if (shouldAggro) {
                        this.setTarget(player);
                    }
                }
            }

            return ActionResult.success(world.isClient);
        }

        if (itemStack.getItem() == Items.ROTTEN_FLESH && !isTamed()) {
            if (world.isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                if (this.random.nextInt(6) == 0) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }

        if (this.isTamed() && this.isBreedingItem(itemStack)) {
            if (this.isBaby()) {
                this.eat(player, hand, itemStack);
                this.growUp(PassiveEntity.toGrowUpAge(-this.getBreedingAge()), true);
                return ActionResult.success(this.getWorld().isClient);
            } else if (this.canEat()) {
                this.eat(player, hand, itemStack);
                this.lovePlayer(player);
                return ActionResult.success(world.isClient);
            }
            if (world.isClient) return ActionResult.CONSUME;
        }

        if (!this.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_KOMODO_DRAGON_RIDING)) {
            if (!world.isClient()) {
                player.sendMessage(Text.translatable("domixs-creatures.message.riding_disabled"), true);
            }
            return ActionResult.FAIL;
        }

        if (!this.isAlive()) {
            return ActionResult.PASS;
        }

        boolean isSaddle = itemStack.isOf(Items.SADDLE);

        if (isSaddle && this.canBeSaddled() && !this.isSaddled() && this.isTamed()) {
            if (!world.isClient()) {
                this.saddle(itemStack, SoundCategory.NEUTRAL);
                this.setPersistent();
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return ActionResult.success(world.isClient);

        } else if (this.isSaddled() && !this.hasPassengers() && itemStack.isEmpty() && player.isSneaking()) {
            if (!world.isClient()) {
                this.dropItem(Items.SADDLE);
                this.unsaddle();
            }
            return ActionResult.success(world.isClient);

        } else if (this.isSaddled() && !this.hasPassengers() && !this.isBreedingItem(itemStack) && !player.hasVehicle()) {
            if (!world.isClient()) {
                player.startRiding(this);
            }
            return ActionResult.success(world.isClient);
        }

        if (itemStack.isOf(Items.BRUSH) && this.hasSnowLayer()) {
            if (!world.isClient) {
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
        return ModEntities.KOMODO_DRAGON.create(world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<KomodoDragonEntity> landController = new AnimationController<>(this, "land_controller", 5, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.komodo_dragon.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_komodo_dragon.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.komodo_dragon.walk", Animation.LoopType.LOOP));
            }
        } else if (this.isBasking()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_komodo_dragon.bask", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.komodo_dragon.bask", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_komodo_dragon.idle", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.komodo_dragon.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_komodo_dragon.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.komodo_dragon.sleep", Animation.LoopType.LOOP));
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
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_komodo_dragon.swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isTouchingWater()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.komodo_dragon.swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public boolean isBasking() {
        return this.dataTracker.get(IS_BASKING);
    }

    @Override
    public void setBasking(boolean basking) {
        this.dataTracker.set(IS_BASKING, basking);
    }

    @Override
    public int getBaskingCooldown() {
        return baskingCooldown;
    }

    @Override
    public void setBaskingCooldown(int ticks) {
        this.baskingCooldown = ticks;
    }

    private static class KomodoDragonLayEggGoal extends MoveToTargetPosGoal {
        private final KomodoDragonEntity komodo;

        KomodoDragonLayEggGoal(KomodoDragonEntity komodo, double speed) {
            super(komodo, speed, 16);
            this.komodo = komodo;
        }

        @Override
        public boolean canStart() {
            return komodo.hasEgg() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && komodo.hasEgg();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.hasReached()) {
                World world = komodo.getWorld();
                BlockPos eggPos = this.targetPos.up();

                if (world.getBlockState(eggPos).isReplaceable()) {
                    world.playSound(null, eggPos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);

                    int eggCount = world.random.nextInt(3) + 1;
                    BlockState eggState = ModBlocks.KOMODO_DRAGON_EGG.getDefaultState().with(KomodoDragonEggBlock.EGGS, eggCount);

                    world.setBlockState(eggPos, eggState, 3);

                    komodo.setHasEgg(false);
                    komodo.setLoveTicks(600);
                } else {
                    this.stop();
                }
            }
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            return world.getBlockState(pos).isIn(ModTags.Blocks.KOMODO_DRAGON_EGG_HATCHABLE)
                    && world.getBlockState(pos.up()).isReplaceable();
        }
    }

    private static class KomodoDragonMateGoal extends AnimalMateGoal {
        private final KomodoDragonEntity komodo;

        KomodoDragonMateGoal(KomodoDragonEntity komodo, double speed) {
            super(komodo, speed);
            this.komodo = komodo;
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !komodo.hasEgg();
        }

        @Override
        protected void breed() {
            if (mate == null) {
                return;
            }

            ServerPlayerEntity serverPlayerEntity = animal.getLovingPlayer();
            if (serverPlayerEntity == null) {
                serverPlayerEntity = mate.getLovingPlayer();
            }

            if (serverPlayerEntity != null) {
                serverPlayerEntity.incrementStat(Stats.ANIMALS_BRED);
                Criteria.BRED_ANIMALS.trigger(serverPlayerEntity, animal, mate, null);
            }

            // Assign the egg to one random komodo
            if (animal.getRandom().nextBoolean()) {
                komodo.setHasEgg(true);
            } else {
                ((KomodoDragonEntity) mate).setHasEgg(true);
            }

            animal.setBreedingAge(6000);
            mate.setBreedingAge(6000);
            animal.resetLoveTicks();
            mate.resetLoveTicks();

            if (world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                var random = animal.getRandom();
                world.spawnEntity(
                        new ExperienceOrbEntity(
                                world,
                                animal.getX(),
                                animal.getY(),
                                animal.getZ(),
                                random.nextInt(7) + 1
                        )
                );
            }
        }
    }
}
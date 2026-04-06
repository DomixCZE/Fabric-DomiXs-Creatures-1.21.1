package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.goal.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.goal.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.goal.entity.RatMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.goal.util.Sleepy;
import net.domixcze.domixscreatures.entity.ai.goal.util.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.rat.RatVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Set;

public class RatEntity extends TameableEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;
    private int cropDestroyCooldown = 0;

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public RatEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,true, false, true, false, 7.0, 600, 800, false, false, true, true, 1));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new RatMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(1, new BabyFollowParentGoal(this, 1.25));
        this.goalSelector.add(1, new FleeGoal<>(this, LivingEntity.class, ModTags.EntityTypes.RAT_FLEE_FROM, 10.0f,1.0,1.2));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.add(2, new DestroyCropGoal(this, 1.0D));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BREEDING) {

            // Assign a random variant
            RatVariants[] variants = RatVariants.values();
            RatVariants variant = variants[this.random.nextInt(variants.length)];
            this.setVariant(variant);
        }

        return entityData;
    }

    //Crazy?

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(SLEEPING, false);
        builder.add(SITTING, false);
        builder.add(VARIANT, RatVariants.BLACK.getId());
    }

    public RatVariants getVariant() {
        return RatVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(RatVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
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
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    @Override
    public void setSitting(boolean sitting) {
        super.setSitting(sitting);
        this.dataTracker.set(SITTING, sitting);
    }

    public int getCropDestroyCooldown() {
        return cropDestroyCooldown;
    }

    public void setCropDestroyCooldown(int ticks) {
        this.cropDestroyCooldown = ticks;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putInt("CropDestroyCooldown", this.cropDestroyCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.setVariant(RatVariants.byId(nbt.getInt("Variant")));
        this.cropDestroyCooldown = nbt.getInt("CropDestroyCooldown");
    }

    //I Was Crazy Once.

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!this.getWorld().isClient() && hand == Hand.MAIN_HAND && this.isTamed() && this.isOwner(player) && !this.isBreedingItem(itemStack)) {
            this.setSitting(!this.isSitting());

            Text entityName = this.getDisplayName();
            Text action = Text.translatable(this.isSitting()
                    ? "message.domixs-creatures.action.sitting"
                    : "message.domixs-creatures.action.following");

            Text message = Text.literal(entityName.getString() + " ")
                    .append(action)
                    .append(".")
                    .styled(style -> style.withColor(Formatting.GREEN));

            player.sendMessage(message, true);

            return ActionResult.SUCCESS;
        }

        if (itemStack.getItem() == ModItems.CHEESE_PIECE && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
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

    @Override
    public void tick() {
        super.tick();

        SnowLayerUtil.handleSnowLayerTick(this, this);

        if (cropDestroyCooldown > 0) cropDestroyCooldown--;
    }

    //They Locked Me In A Room.

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<RatEntity> landController = new AnimationController<>(this, "land_controller", 5, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.rat.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_rat.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_rat.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.rat.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.rat.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_rat.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.rat.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_rat.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.rat.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    //SOUNDS
    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return ModSounds.RAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.RAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.RAT_DEATH;
    }

    //A Rubber Room.

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.RAT_FOR_BREEDING);
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RatEntity baby = ModEntities.RAT.create(world);
        if (baby == null) return null;

        RatEntity parent1 = this;
        RatEntity parent2 = (RatEntity) entity;

        RatVariants v1 = parent1.getVariant();
        RatVariants v2 = parent2.getVariant();

        RatVariants result;

        if (v1 == v2) {
            result = v1;
        } else {

            Set<RatVariants> combo = Set.of(v1, v2);

            // BLACK + WHITE
            if (combo.contains(RatVariants.BLACK) && combo.contains(RatVariants.WHITE)) {
                RatVariants[] options = new RatVariants[]{
                        RatVariants.GRAY,
                        RatVariants.WHITE_BLACK_SPOTS,
                        RatVariants.WHITE_BLACK
                };
                result = options[world.getRandom().nextInt(options.length)];
            }

            // GRAY + WHITE
            else if (combo.contains(RatVariants.GRAY) && combo.contains(RatVariants.WHITE)) {
                result = RatVariants.GRAY_WHITE;
            }

            // BROWN + WHITE
            else if (combo.contains(RatVariants.BROWN) && combo.contains(RatVariants.WHITE)) {
                result = RatVariants.BROWN_WHITE;
            }

            // DARK BROWN + WHITE
            else if (combo.contains(RatVariants.DARK_BROWN) && combo.contains(RatVariants.WHITE)) {
                result = RatVariants.WHITE_BROWN;
            }

            // BROWN + BLACK
            else if (combo.contains(RatVariants.BROWN) && combo.contains(RatVariants.BLACK)) {
                result = RatVariants.DARK_BROWN;
            }

            else {
                result = world.getRandom().nextBoolean() ? v1 : v2;
            }
        }
        if (this.isTamed()) {
            baby.setOwnerUuid(this.getOwnerUuid());
            baby.setTamed(true, true);
        }

        baby.setVariant(result);
        return baby;
    }

    //A Rubber Room With Rats.

    public static class DestroyCropGoal extends MoveToTargetPosGoal {
        private final RatEntity rat;
        private static final int COOLDOWN_TICKS = 1200; // 1 minute

        public DestroyCropGoal(RatEntity rat, double speed) {
            super(rat, speed, 10, 1);
            this.rat = rat;
        }

        @Override
        public boolean canStart() {
            if (rat.isTamed() || rat.getCropDestroyCooldown() > 0
                    || !this.rat.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)
                    || !rat.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_RAT_CROP_BREAKING)) {
                return false;
            }

            return this.findTargetPos();
        }

        @Override
        protected void startMovingToTarget() {
            this.rat.getNavigation().startMovingTo(
                    (double)this.targetPos.getX() + 0.5,
                    this.targetPos.getY(),
                    (double)this.targetPos.getZ() + 0.5,
                    this.speed
            );
        }

        @Override
        public void tick() {
            super.tick();

            BlockPos target = this.getTargetPos();
            BlockPos cropPos = target.down();

            double distanceSq = this.rat.squaredDistanceTo(cropPos.toCenterPos());

            if (distanceSq < 1.0D) {

                if (!rat.getWorld().isClient()) {
                    BlockState targetState = rat.getWorld().getBlockState(cropPos);

                    if (targetState.getBlock() instanceof CropBlock crop && crop.isMature(targetState)) {

                        rat.getWorld().breakBlock(cropPos, true);
                        rat.setCropDestroyCooldown(COOLDOWN_TICKS);
                    }
                }

                this.stop();
            }
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos) {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() instanceof CropBlock crop && crop.isMature(state);
        }
    }

    //And Rats Make Me Crazy

    static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
        private final RatEntity rat;

        public FleeGoal(RatEntity rat, Class<T> fleeFromType, TagKey<EntityType<?>> fleeFromTag, float distance, double slowSpeed, double fastSpeed) {
            super(rat, fleeFromType, distance, slowSpeed, fastSpeed,
                    (targetEntity) -> {
                        boolean isInTag = targetEntity != null && targetEntity.getType().isIn(fleeFromTag);
                        boolean passesDefaultChecks = EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(targetEntity);
                        return isInTag && passesDefaultChecks;
                    });
            this.rat = rat;
        }

        @Override
        public boolean canStart() {
            return !this.rat.isSleeping() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.rat.isSleeping() && super.shouldContinue();
        }
    }
}
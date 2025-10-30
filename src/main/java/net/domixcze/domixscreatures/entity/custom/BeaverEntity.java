package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.beaver.BeaverVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.AmphibiousSwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class BeaverEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public BeaverEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new BeaverMoveControl();
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.2);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,false, true, true, false, 3.0, 500, 700, true, false, false, true, 1));
        this.goalSelector.add( 1, new StripLogGoal(this, 1,10));
        this.goalSelector.add(1, new StayNearHomeGoal(this, 1.0, 16, 10));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(HOME_POS, BlockPos.ORIGIN);
        builder.add(SLEEPING, false);
        builder.add(VARIANT, BeaverVariants.BROWN.getId());
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    public boolean isSleeping() {
        return this.dataTracker.get(SLEEPING);
    }

    public void setSleeping(boolean sleeping) {
        this.dataTracker.set(SLEEPING, sleeping);
        if (sleeping) {
            this.getNavigation().stop();
        }
    }

    public BeaverVariants getVariant() {
        return BeaverVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(BeaverVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.BEAVER_FOR_BREEDING);
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.4F, 0.3F);
        }
        return this.getType().getDimensions();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        BeaverEntity baby = (BeaverEntity) ModEntities.BEAVER.create(world);
        if (baby != null) {
            BeaverEntity parent1 = this;
            BeaverEntity parent2 = (BeaverEntity) entity;

            if (parent1.getVariant() == BeaverVariants.ALBINO && parent2.getVariant() == BeaverVariants.ALBINO) {
                baby.setVariant(BeaverVariants.ALBINO); // 100% if both parents are albino
            } else {
                double chance = 0.01; // Base 1% chance

                if (parent1.getVariant() == BeaverVariants.ALBINO || parent2.getVariant() == BeaverVariants.ALBINO) {
                    chance = 0.25; // 25% if one parent is albino
                }

                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(BeaverVariants.ALBINO);
                } else {
                    baby.setVariant(BeaverVariants.BROWN);
                }
            }
        }
        return baby;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.beaver.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.beaver.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.idle_swim", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.beaver.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.beaver.idle_swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.beaver.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    protected EntityNavigation createNavigation(World world) {
        return new AmphibiousSwimNavigation(this, world);
    }

    class BeaverMoveControl extends MoveControl {
        public BeaverMoveControl() {
            super(BeaverEntity.this);
        }
    }

    public boolean isPushedByFluids() {
        return false;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (this.isOnGround()) {
                movementSpeed.setBaseValue(0.2f);
            } else if (this.isTouchingWater()) {
                movementSpeed.setBaseValue(1.5f);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isTouchingWater()) {
            this.moveControl = new AquaticMoveControl(this, 85, 10, 5F, 0.1F, true);
        } else if (this.isOnGround()) {
            this.moveControl = new BeaverMoveControl();
        }

        SnowLayerUtil.handleSnowLayerTick(this, this);
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

    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }

    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("Sleeping", this.isSleeping());
        BlockPos homePos = this.getHomePos();
        if (!homePos.equals(BlockPos.ORIGIN)) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setVariant(BeaverVariants.byId(nbt.getInt("Variant")));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        if (nbt.contains("HomeX") && nbt.contains("HomeY") && nbt.contains("HomeZ")) {
            this.setHomePos(new BlockPos(nbt.getInt("HomeX"), nbt.getInt("HomeY"), nbt.getInt("HomeZ")));
        }
    }

    public static final Map<Block, Block> STRIPPED_LOGS = new HashMap<>();

    static {
        STRIPPED_LOGS.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
        STRIPPED_LOGS.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        STRIPPED_LOGS.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG);
        STRIPPED_LOGS.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG);
        STRIPPED_LOGS.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG);
        STRIPPED_LOGS.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
        STRIPPED_LOGS.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG);
        STRIPPED_LOGS.put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG);

        STRIPPED_LOGS.put(ModBlocks.SPECTRAL_LOG, ModBlocks.STRIPPED_SPECTRAL_LOG);
        STRIPPED_LOGS.put(ModBlocks.PALM_LOG, ModBlocks.STRIPPED_PALM_LOG);
    }

    public static class StripLogGoal extends Goal {
        private final BeaverEntity beaver;
        private final double speed;
        private final int searchRadius;
        private BlockPos targetLogPos;
        private final Random random = new Random();
        private final World world;

        public StripLogGoal(BeaverEntity beaver, double speed, int searchRadius) {
            this.beaver = beaver;
            this.speed = speed;
            this.searchRadius = searchRadius;
            this.world = beaver.getWorld();
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || !ModConfig.INSTANCE.enableBeaverLogStripping) {
                return false;
            }

            if (beaver.getRandom().nextInt(100) < 5) {
                BlockPos beaverPos = beaver.getBlockPos();
                World world = beaver.getWorld();

                for (BlockPos pos : BlockPos.iterateOutwards(beaverPos, searchRadius, searchRadius, searchRadius)) {
                    BlockState state = world.getBlockState(pos);
                    if (isLog(state) && isReachable(beaverPos, pos)) {
                        this.targetLogPos = pos;
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return this.targetLogPos != null && isLog(beaver.getWorld().getBlockState(this.targetLogPos));
        }

        @Override
        public void start() {
            if (this.targetLogPos != null) {
                beaver.getNavigation().startMovingTo(this.targetLogPos.getX() + 0.5, this.targetLogPos.getY(), this.targetLogPos.getZ() + 0.5, this.speed);
            }
        }

        @Override
        public void tick() {
            if (this.targetLogPos != null && beaver.getBlockPos().isWithinDistance(this.targetLogPos, 1.5)) {
                stripLog(this.targetLogPos);
                this.targetLogPos = null;
            }
        }

        @Override
        public void stop() {
            this.targetLogPos = null;
        }

        private boolean isLog(BlockState state) {
            return state.isIn(BlockTags.LOGS);
        }

        private boolean isReachable(BlockPos beaverPos, BlockPos logPos) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (beaverPos.offset(direction).equals(logPos)) {
                    return true;
                }
            }
            return false;
        }

        private void stripLog(BlockPos logPos) {
            World world = beaver.getWorld();
            BlockState currentState = world.getBlockState(logPos);
            Block strippedLog = getStrippedLog(currentState.getBlock());

            if (strippedLog != null) {
                // Preserve rotation
                BlockState newState = strippedLog.getDefaultState();
                if (currentState.contains(PillarBlock.AXIS)) {
                    newState = newState.with(PillarBlock.AXIS, currentState.get(PillarBlock.AXIS));
                }

                world.setBlockState(logPos, newState);

                world.playSound(
                        null,
                        logPos,
                        SoundEvents.ITEM_AXE_STRIP,
                        SoundCategory.BLOCKS,
                        1.0F,
                        1.0F
                );

                if (random.nextDouble() < 0.2) { //20%
                    Block.dropStack(world, logPos, new ItemStack(ModItems.SAWDUST));
                }
                Block.dropStack(world, logPos, new ItemStack(ModItems.BARK, random.nextInt(2) + 1));
            }
        }

        private Block getStrippedLog(Block log) {
            return STRIPPED_LOGS.get(log);
        }
    }

    public static class StayNearHomeGoal extends Goal {
        private final BeaverEntity beaver;
        private final double speed;
        private final int innerRadius;
        private final int totalRadius;

        private BlockPos targetWaypoint;

        public StayNearHomeGoal(BeaverEntity beaver, double speed, int innerRadius, int outerRadiusOffset) {
            this.beaver = beaver;
            this.speed = speed;
            this.innerRadius = innerRadius;
            this.totalRadius = innerRadius + outerRadiusOffset;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (beaver.isLeashed()) return false;

            BlockPos currentHome = beaver.getHomePos();

            if (currentHome.equals(BlockPos.ORIGIN) || isHomeBlockValid(currentHome)) {
                findNearestPileOfSticksAroundBeaver(totalRadius).ifPresentOrElse(
                        beaver::setHomePos,
                        () -> beaver.setHomePos(BlockPos.ORIGIN)
                );
                currentHome = beaver.getHomePos();
            }

            if (currentHome.equals(BlockPos.ORIGIN)) {
                return false;
            }

            double distSqToHome = beaver.getBlockPos().getSquaredDistance(currentHome);

            return distSqToHome > innerRadius * innerRadius;
        }

        @Override
        public void start() {
            this.targetWaypoint = findRandomPositionNearHome(beaver.getHomePos(), innerRadius);

            if (this.targetWaypoint != null) {
                beaver.getNavigation().startMovingTo(
                        this.targetWaypoint.getX() + 0.5, this.targetWaypoint.getY(), this.targetWaypoint.getZ() + 0.5, speed
                );
                beaver.getLookControl().lookAt(
                        this.targetWaypoint.getX() + 0.5, this.targetWaypoint.getY() + 0.5, this.targetWaypoint.getZ() + 0.5
                );
            } else {
                beaver.getNavigation().stop();
            }
        }

        @Override
        public boolean shouldContinue() {
            if (beaver.isLeashed()) return false;

            BlockPos currentHome = beaver.getHomePos();
            if (currentHome.equals(BlockPos.ORIGIN) || isHomeBlockValid(currentHome)) {
                return false;
            }

            double distSqToHome = beaver.getBlockPos().getSquaredDistance(currentHome);

            if (distSqToHome > totalRadius * totalRadius) {
                if (!beaver.getWorld().isClient()) {
                    beaver.setHomePos(BlockPos.ORIGIN);
                }
                return false;
            }

            if (distSqToHome <= innerRadius * innerRadius) {
                return false;
            }

            if (beaver.getNavigation().isIdle()) {
                this.targetWaypoint = findRandomPositionNearHome(currentHome, innerRadius);
                if (this.targetWaypoint != null) {
                    beaver.getNavigation().startMovingTo(
                            this.targetWaypoint.getX() + 0.5, this.targetWaypoint.getY(), this.targetWaypoint.getZ() + 0.5, speed
                    );
                    beaver.getLookControl().lookAt(
                            this.targetWaypoint.getX() + 0.5, this.targetWaypoint.getY() + 0.5, this.targetWaypoint.getZ() + 0.5
                    );
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void stop() {
            beaver.getNavigation().stop();
            this.targetWaypoint = null;
        }

        private boolean isHomeBlockValid(BlockPos homePos) {
            return !beaver.getWorld().isChunkLoaded(homePos) || !beaver.getWorld().getBlockState(homePos).isOf(ModBlocks.PILE_OF_STICKS_BLOCK);
        }

        private Optional<BlockPos> findNearestPileOfSticksAroundBeaver(int searchRadius) {
            BlockPos beaverPos = beaver.getBlockPos();
            World world = beaver.getWorld();
            Box searchBox = new Box(beaverPos).expand(searchRadius);

            for (BlockPos p : BlockPos.iterate(
                    (int) searchBox.minX, (int) searchBox.minY, (int) searchBox.minZ,
                    (int) searchBox.maxX, (int) searchBox.maxY, (int) searchBox.maxZ)) {
                if (world.getBlockState(p).isOf(ModBlocks.PILE_OF_STICKS_BLOCK)) {
                    return Optional.of(p);
                }
            }
            return Optional.empty();
        }

        private BlockPos findRandomPositionNearHome(BlockPos center, int radius) {
            if (center.equals(BlockPos.ORIGIN)) return null;

            World world = beaver.getWorld();
            for (int i = 0; i < 10; i++) {
                BlockPos randomPos = center.add(
                        world.getRandom().nextInt(radius * 2 + 1) - radius,
                        world.getRandom().nextInt(3) - 1,
                        world.getRandom().nextInt(radius * 2 + 1) - radius
                );

                BlockState blockState = world.getBlockState(randomPos);
                BlockState blockBelowState = world.getBlockState(randomPos.down());

                if ((blockState.isAir() || blockState.isReplaceable()) && blockBelowState.isSolid()) {
                    return randomPos;
                }
            }
            return null;
        }
    }
}

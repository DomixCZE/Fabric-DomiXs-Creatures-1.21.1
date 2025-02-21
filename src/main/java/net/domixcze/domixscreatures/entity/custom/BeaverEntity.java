package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.beaver.BeaverVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;

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
        this.setStepHeight(1.2f);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, false, true, true, false, 3.0, 500, 700, true, false, false, true));
        this.goalSelector.add( 1, new StripLogGoal(this, 1,10));
        this.goalSelector.add(1, new StayNearHomeGoal(this, 1.0, 16, 10));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.65F;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAS_SNOW_LAYER, false);
        this.dataTracker.startTracking(HOME_POS, BlockPos.ORIGIN);
        this.dataTracker.startTracking(SLEEPING, false);
        this.dataTracker.startTracking(VARIANT, BeaverVariants.BROWN.getId());
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
        return stack.isOf(ModItems.WATER_LILY);
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

        if (itemStack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemStack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
                }

                this.playSound(SoundEvents.BLOCK_SNOW_BREAK, 1.0F, 1.0F);

                if (!this.getWorld().isClient) {
                    int count = 3 + this.getWorld().random.nextInt(2);
                    this.dropStack(new ItemStack(Items.SNOWBALL, count));
                }

                return ActionResult.SUCCESS;
            }
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
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
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
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_beaver.idle_swim", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
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

    public boolean canBreatheInWater() {
        return true;
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

        if (this.isSleeping()) {
            this.getNavigation().stop();
        }

        boolean isSnowing = this.getWorld().isRaining() && isInSnowyBiome();

        if (this.isInSnowyBiome() && isSnowing && !this.hasSnowLayer()) {
            snowTicks++;
            if (snowTicks >= 600) {
                this.setHasSnowLayer(true);
                snowTicks = 0;
            }
        }

        if ((this.isTouchingWater() || !this.isInSnowyBiome()) && this.hasSnowLayer()) {
            snowMeltTimer++;
            if (snowMeltTimer >= 200) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;
            }
        }
    }

    public boolean isInSnowyBiome() {
        BlockPos pos = this.getBlockPos();
        RegistryEntry<Biome> biomeEntry = this.getWorld().getBiome(pos);
        Biome biome = biomeEntry.value();
        return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
    }

    public boolean hasSnowLayer() {
        return this.dataTracker.get(HAS_SNOW_LAYER);
    }

    public void setHasSnowLayer(boolean hasSnow) {
        this.dataTracker.set(HAS_SNOW_LAYER, hasSnow);
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

    public static class StripLogGoal extends Goal {
        private final BeaverEntity beaver;
        private final double speed;
        private final int searchRadius;
        private BlockPos targetLogPos;
        private final Random random = new Random();

        public StripLogGoal(BeaverEntity beaver, double speed, int searchRadius) {
            this.beaver = beaver;
            this.speed = speed;
            this.searchRadius = searchRadius;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.beaver.getRandom().nextInt(100) < 5) {
                BlockPos beaverPos = this.beaver.getBlockPos();
                World world = this.beaver.getWorld();

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
                this.beaver.getNavigation().startMovingTo(this.targetLogPos.getX() + 0.5, this.targetLogPos.getY(), this.targetLogPos.getZ() + 0.5, this.speed);
            }
        }

        @Override
        public void tick() {
            if (this.targetLogPos != null && this.beaver.getBlockPos().isWithinDistance(this.targetLogPos, 1.5)) {
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
            World world = this.beaver.getWorld();
            BlockState currentState = world.getBlockState(logPos);
            Block strippedLog = getStrippedLog(currentState.getBlock());

            if (strippedLog != null) {
                // Preserve rotation
                BlockState newState = strippedLog.getDefaultState();
                if (currentState.contains(PillarBlock.AXIS)) {
                    newState = newState.with(PillarBlock.AXIS, currentState.get(PillarBlock.AXIS));
                }

                world.setBlockState(logPos, newState);

                if (random.nextDouble() < 0.2) { //20%
                    Block.dropStack(world, logPos, new ItemStack(ModItems.SAWDUST));
                }
                Block.dropStack(world, logPos, new ItemStack(ModItems.BARK, random.nextInt(2) + 1));
            }
        }

        private Block getStrippedLog(Block log) {
            if (log == Blocks.OAK_LOG) return Blocks.STRIPPED_OAK_LOG;
            if (log == Blocks.SPRUCE_LOG) return Blocks.STRIPPED_SPRUCE_LOG;
            if (log == Blocks.BIRCH_LOG) return Blocks.STRIPPED_BIRCH_LOG;
            if (log == Blocks.JUNGLE_LOG) return Blocks.STRIPPED_JUNGLE_LOG;
            if (log == Blocks.ACACIA_LOG) return Blocks.STRIPPED_ACACIA_LOG;
            if (log == Blocks.DARK_OAK_LOG) return Blocks.STRIPPED_DARK_OAK_LOG;
            return null;
        }
    }

    public static class StayNearHomeGoal extends Goal {
        private final BeaverEntity beaver;
        private final double speed;
        private final int range;
        private final int tolerance;

        public StayNearHomeGoal(BeaverEntity beaver, double speed, int range, int tolerance) {
            this.beaver = beaver;
            this.speed = speed;
            this.range = range;
            this.tolerance = tolerance;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (beaver.isLeashed()) {
                return false;
            }

            BlockPos homePos = beaver.getHomePos();
            if (homePos.equals(BlockPos.ORIGIN) || !isHomeBlockValid(homePos)) {
                Optional<BlockPos> nearestBlock = findNearestPileOfSticks();
                if (nearestBlock.isPresent()) {
                    beaver.setHomePos(nearestBlock.get());
                } else {
                    beaver.setHomePos(BlockPos.ORIGIN);
                }
            }

            if (!beaver.getHomePos().equals(BlockPos.ORIGIN)) {
                double distance = beaver.getBlockPos().getSquaredDistance(beaver.getHomePos());
                return distance > range * range || (distance > range * range && distance <= (range + tolerance) * (range + tolerance));
            }

            return false;
        }

        @Override
        public void start() {
            if (beaver.isLeashed()) {
                return;
            }

            BlockPos homePos = beaver.getHomePos();
            if (!homePos.equals(BlockPos.ORIGIN)) {
                BlockPos targetPos = findRandomPositionNearHome();
                if (targetPos != null) {
                    beaver.getNavigation().startMovingTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
                    beaver.getLookControl().lookAt(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                }
            }
        }

        @Override
        public boolean shouldContinue() {
            if (beaver.isLeashed()) {
                return false;
            }

            BlockPos homePos = beaver.getHomePos();
            if (!homePos.equals(BlockPos.ORIGIN)) {
                double distance = beaver.getBlockPos().getSquaredDistance(homePos);
                if (distance > (range + tolerance) * (range + tolerance)) {
                    Optional<BlockPos> nearestBlock = findNearestPileOfSticks();
                    if (nearestBlock.isPresent()) {
                        beaver.setHomePos(nearestBlock.get());
                    } else {
                        beaver.setHomePos(BlockPos.ORIGIN);
                    }
                    return false;
                } else if (distance > range * range && distance <= (range + tolerance) * (range + tolerance)) {
                    BlockPos targetPos = findRandomPositionNearHome();
                    if (targetPos != null) {
                        beaver.getNavigation().startMovingTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, speed);
                    }
                }
                return !beaver.getNavigation().isIdle();
            }
            return false;
        }

        @Override
        public void stop() {
            if (beaver.isLeashed()) {
                return;
            }

            BlockPos homePos = beaver.getHomePos();
            if (!homePos.equals(BlockPos.ORIGIN)) {
                if (!isHomeBlockValid(homePos)) {
                    Optional<BlockPos> nearestBlock = findNearestPileOfSticks();
                    if (nearestBlock.isPresent()) {
                        beaver.setHomePos(nearestBlock.get());
                    } else {
                        beaver.setHomePos(BlockPos.ORIGIN);
                    }
                }
            }
        }

        private boolean isHomeBlockValid(BlockPos homePos) {
            return beaver.getWorld().getBlockState(homePos).isOf(ModBlocks.PILE_OF_STICKS_BLOCK);
        }

        private Optional<BlockPos> findNearestPileOfSticks() {
            BlockPos beaverPos = beaver.getBlockPos();
            World world = beaver.getWorld();
            Box searchBox = new Box(beaverPos).expand(range + tolerance);

            for (BlockPos pos : BlockPos.iterate((int) searchBox.minX, (int) searchBox.minY, (int) searchBox.minZ,
                    (int) searchBox.maxX, (int) searchBox.maxY, (int) searchBox.maxZ)) {
                if (world.getBlockState(pos).isOf(ModBlocks.PILE_OF_STICKS_BLOCK)) {
                    return Optional.of(pos);
                }
            }
            return Optional.empty();
        }

        private BlockPos findRandomPositionNearHome() {
            BlockPos homePos = beaver.getHomePos();
            if (!homePos.equals(BlockPos.ORIGIN)) {
                World world = beaver.getWorld();
                for (int i = 0; i < 10; i++) { // Try 10 random positions
                    BlockPos randomPos = homePos.add(
                            world.getRandom().nextInt(range * 2 + 1) - range,
                            0,
                            world.getRandom().nextInt(range * 2 + 1) - range);

                    if (world.getBlockState(randomPos).isAir()) {
                        return randomPos;
                    }
                }
            }
            return null;
        }
    }
}

package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.HollowLogBlock;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.entity.ai.ColonyMember;
import net.domixcze.domixscreatures.entity.ai.ReturnToColonyGoal;
import net.domixcze.domixscreatures.entity.ai.StayNearColonyGoal;
import net.domixcze.domixscreatures.entity.ai.TermiteMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.client.termite.TermiteVariants;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class TermiteEntity extends AnimalEntity implements GeoEntity, ColonyMember {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(TermiteEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TermiteEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_SAWDUST = DataTracker.registerData(TermiteEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> EATING = DataTracker.registerData(TermiteEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int eatingTimer = 0;
    private int eatCooldown = 0;

    public TermiteEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new TermiteMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(1, new TermiteEatLogGoal(this));
        this.goalSelector.add(2, new ReturnToColonyGoal<>(this, 1.0));
        this.goalSelector.add(3, new StayNearColonyGoal<>(this, ModBlocks.TERMITE_NEST_BLOCK, 1.0, 8, 16));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(0, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, AntEntity.class, true));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(EATING, false);
        builder.add(HAS_SAWDUST, false);
        builder.add(VARIANT, TermiteVariants.WORKER.getId());
        builder.add(HOME_POS, BlockPos.ORIGIN);
    }

    public TermiteVariants getVariant() {
        return TermiteVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(TermiteVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());

        var healthAttr = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            double value = switch (variant) {
                case SOLDIER -> 12.0;
                case QUEEN -> 20.0;
                default -> 6.0;
            };
            healthAttr.setBaseValue(value);

            this.setHealth((float)value);
        }

        var speedAttr = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(variant == TermiteVariants.QUEEN ? 0.1D : 0.2D);
        }
    }

    public void setHasSawdust(boolean hasSawdust) {
        this.dataTracker.set(HAS_SAWDUST, hasSawdust);
    }

    public boolean hasSawdust() {
        return this.dataTracker.get(HAS_SAWDUST);
    }

    public void setEating(boolean eating) {
        this.dataTracker.set(EATING, eating);
    }

    public boolean isEating() {
        return this.dataTracker.get(EATING);
    }

    public void setEatingTimer(int ticks) {
        this.eatingTimer = ticks;
    }

    public int getEatingTimer() {
        return this.eatingTimer;
    }

    public void setEatCooldown(int ticks) {
        this.eatCooldown = ticks;
    }

    public int getEatCooldown() {
        return this.eatCooldown;
    }

    @Override
    public void setHomePos(BlockPos pos) {
        this.dataTracker.set(HOME_POS, pos);
    }

    @Override
    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putInt("EatingTimer", this.eatingTimer);
        nbt.putBoolean("IsEating", isEating());
        nbt.putInt("EatCooldown", this.eatCooldown);
        nbt.putBoolean("HasSawdust", hasSawdust());
        BlockPos homePos = this.getHomePos();
        if (homePos != null && !homePos.equals(BlockPos.ORIGIN)) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(TermiteVariants.byId(nbt.getInt("Variant")));
        this.eatingTimer = nbt.getInt("EatingTimer");
        setEating(nbt.getBoolean("IsEating"));
        this.eatCooldown = nbt.getInt("EatCooldown");
        setHasSawdust(nbt.getBoolean("HasSawdust"));
        if (nbt.contains("HomeX") && nbt.contains("HomeY") && nbt.contains("HomeZ")) {
            this.setHomePos(new BlockPos(nbt.getInt("HomeX"), nbt.getInt("HomeY"), nbt.getInt("HomeZ")));
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!getWorld().isClient) {
            if (reason.shouldDestroy() || reason == RemovalReason.CHANGED_DIMENSION) {
                BlockPos home = getHomePos();
                if (home != null && !home.equals(BlockPos.ORIGIN)) {
                    if (getWorld().getBlockEntity(home) instanceof AbstractColonyBlockEntity colony) {
                        colony.unregisterMember(getUuid());
                    }
                }
            }
        }
        super.remove(reason);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.COMMAND) {
            double chance = this.random.nextDouble();

            if (chance < 0.10) {
                this.setVariant(TermiteVariants.QUEEN);
            } else if (chance < 0.25) {
                this.setVariant(TermiteVariants.SOLDIER);
            } else {
                this.setVariant(TermiteVariants.WORKER);
            }
        }

        return entityData;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && this.eatCooldown > 0) {
            this.eatCooldown--;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        AnimationController<TermiteEntity> landController = new AnimationController<>(this, "land_controller", 5, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.termite.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.termite.walk", Animation.LoopType.LOOP));
        } else if (this.isEating()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.termite.eat", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.termite.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    public static final Map<Block, Block> HOLLOW_LOGS = new HashMap<>();

    static {
        HOLLOW_LOGS.put(Blocks.ACACIA_LOG, ModBlocks.HOLLOW_ACACIA_LOG);
        HOLLOW_LOGS.put(Blocks.OAK_LOG, ModBlocks.HOLLOW_OAK_LOG);
        HOLLOW_LOGS.put(Blocks.BIRCH_LOG, ModBlocks.HOLLOW_BIRCH_LOG);
        HOLLOW_LOGS.put(Blocks.SPRUCE_LOG, ModBlocks.HOLLOW_SPRUCE_LOG);
        HOLLOW_LOGS.put(Blocks.DARK_OAK_LOG, ModBlocks.HOLLOW_DARK_OAK_LOG);
        HOLLOW_LOGS.put(Blocks.JUNGLE_LOG, ModBlocks.HOLLOW_JUNGLE_LOG);
        HOLLOW_LOGS.put(Blocks.MANGROVE_LOG, ModBlocks.HOLLOW_MANGROVE_LOG);
        HOLLOW_LOGS.put(Blocks.CHERRY_LOG, ModBlocks.HOLLOW_CHERRY_LOG);

        HOLLOW_LOGS.put(ModBlocks.PALM_LOG, ModBlocks.HOLLOW_PALM_LOG);
        HOLLOW_LOGS.put(ModBlocks.SPECTRAL_LOG, ModBlocks.HOLLOW_SPECTRAL_LOG);

        HOLLOW_LOGS.put(Blocks.STRIPPED_ACACIA_LOG, ModBlocks.STRIPPED_HOLLOW_ACACIA_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_OAK_LOG, ModBlocks.STRIPPED_HOLLOW_OAK_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_BIRCH_LOG, ModBlocks.STRIPPED_HOLLOW_BIRCH_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_SPRUCE_LOG, ModBlocks.STRIPPED_HOLLOW_SPRUCE_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_DARK_OAK_LOG, ModBlocks.STRIPPED_HOLLOW_DARK_OAK_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_JUNGLE_LOG, ModBlocks.STRIPPED_HOLLOW_JUNGLE_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_MANGROVE_LOG, ModBlocks.STRIPPED_HOLLOW_MANGROVE_LOG);
        HOLLOW_LOGS.put(Blocks.STRIPPED_CHERRY_LOG, ModBlocks.STRIPPED_HOLLOW_CHERRY_LOG);

        HOLLOW_LOGS.put(ModBlocks.STRIPPED_PALM_LOG, ModBlocks.STRIPPED_HOLLOW_PALM_LOG);
        HOLLOW_LOGS.put(ModBlocks.STRIPPED_SPECTRAL_LOG, ModBlocks.STRIPPED_HOLLOW_SPECTRAL_LOG);
    }

    public static class TermiteEatLogGoal extends Goal {
        private final TermiteEntity termite;
        private final World world;
        private BlockPos targetLog;
        private int ticksTrying;
        private final int EAT_TIME = 30; // 1.5 seconds

        public TermiteEatLogGoal(TermiteEntity termite) {
            this.termite = termite;
            this.world = termite.getWorld();
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || !this.world.getGameRules().getBoolean(ModGameRules.ENABLE_TERMITE_LOG_HOLLOWING)) {
                return false;
            }

            if (termite.getHomePos() != BlockPos.ORIGIN) {
                double distSq = termite.squaredDistanceTo(termite.getHomePos().getX() + 0.5, termite.getY(), termite.getHomePos().getZ() + 0.5);
                if (distSq > 256) return false;
            }
            if (termite.getVariant() != TermiteVariants.WORKER) return false;
            if (termite.hasSawdust()) return false;
            if (termite.getEatCooldown() > 0) return false;

            if (targetLog != null || !termite.isOnGround()) return false;
            if (termite.getRandom().nextInt(15) != 0) return false;

            this.targetLog = findTargetBlock();
            return this.targetLog != null;
        }

        @Override
        public void start() {
            this.ticksTrying = 0;
            termite.setEatingTimer(0);
            termite.setEating(false);
            termite.getNavigation().startMovingTo(targetLog.getX() + 0.5, targetLog.getY(), targetLog.getZ() + 0.5, 1.0D);
        }

        @Override
        public void tick() {
            if (targetLog == null) return;

            BlockState currentState = world.getBlockState(targetLog);
            BlockState aboveState = world.getBlockState(targetLog.up());
            boolean hasValidTarget = HOLLOW_LOGS.containsKey(currentState.getBlock()) ||
                    (currentState.getBlock() instanceof HollowLogBlock && HOLLOW_LOGS.containsKey(aboveState.getBlock()));

            if (!hasValidTarget) {
                this.stop();
                return;
            }

            if (termite.getEatingTimer() == 0) {
                ticksTrying++;
                double distSq = termite.squaredDistanceTo(targetLog.getX() + 0.5, termite.getY(), targetLog.getZ() + 0.5);

                if (distSq <= 1.44 || (termite.getNavigation().isIdle() && distSq <= 2.5)) {
                    termite.getNavigation().stop();

                    BlockState state = world.getBlockState(targetLog);
                    if (state.getBlock() instanceof HollowLogBlock && state.get(PillarBlock.AXIS) == Direction.Axis.Y) {
                        termite.refreshPositionAndAngles(targetLog.getX() + 0.5, termite.getY(), targetLog.getZ() + 0.5, termite.getYaw(), termite.getPitch());
                    }

                    this.world.playSound(null, termite.getX(), termite.getY(), termite.getZ(),
                            ModSounds.TERMITE_EAT, termite.getSoundCategory(), 1.0F, 1.0F);

                    termite.setEating(true);
                    termite.setEatingTimer(1);
                }
                if (ticksTrying > 200) this.stop();
            }
            else {
                termite.setEatingTimer(termite.getEatingTimer() + 1);

                if (termite.getEatingTimer() >= EAT_TIME) {
                    if (tryEatUpward()) {
                        termite.setHasSawdust(true);
                        termite.setEatCooldown(1200);
                        BlockState logState = world.getBlockState(targetLog);
                        boolean isVertical = logState.contains(PillarBlock.AXIS) && logState.get(PillarBlock.AXIS) == Direction.Axis.Y;

                        boolean isInside = Math.floor(termite.getX()) == targetLog.getX() && Math.floor(termite.getZ()) == targetLog.getZ();

                        if (isInside && isVertical) {
                            BlockPos currentPos = termite.getBlockPos();
                            BlockPos safeExit = findSafeExit(currentPos);
                            if (safeExit != null) {
                                termite.refreshPositionAndAngles(
                                        safeExit.getX() + 0.5,
                                        currentPos.getY(),
                                        safeExit.getZ() + 0.5,
                                        termite.getYaw(),
                                        termite.getPitch()
                                );
                            }
                        }
                    }
                    this.stop();
                }
            }
        }

        private BlockPos findSafeExit(BlockPos currentPos) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                BlockPos neighbor = currentPos.offset(dir);
                BlockState sideState = world.getBlockState(neighbor);

                boolean isSpaceFree = sideState.isAir() || !sideState.isFullCube(world, neighbor);

                boolean hasFloor = !world.getBlockState(neighbor.down()).isAir();

                if (isSpaceFree && hasFloor) {
                    return neighbor;
                }
            }
            return null;
        }

        private boolean tryEatUpward() {
            for (int i = 0; i < 5; i++) {
                BlockPos scanPos = targetLog.up(i);
                BlockState scanState = world.getBlockState(scanPos);

                if (scanState.getBlock() instanceof HollowLogBlock) continue;

                Block hollowVariant = HOLLOW_LOGS.get(scanState.getBlock());

                if (hollowVariant != null) {
                    BlockState hollowState = hollowVariant.getDefaultState()
                            .with(PillarBlock.AXIS, scanState.get(PillarBlock.AXIS));
                    world.setBlockState(scanPos, hollowState);
                    return true;
                }
                break;
            }
            return false;
        }

        protected BlockPos findTargetBlock() {
            BlockPos origin = termite.getBlockPos();
            for (BlockPos pos : BlockPos.iterate(origin.add(-8, -2, -8), origin.add(8, 2, 8))) {
                BlockState state = world.getBlockState(pos);

                if (HOLLOW_LOGS.containsKey(state.getBlock())) {
                    if (!world.getBlockState(pos.down()).isAir()) return pos.toImmutable();
                }

                if (state.getBlock() instanceof HollowLogBlock && state.get(PillarBlock.AXIS) == Direction.Axis.Y) {
                    BlockState above = world.getBlockState(pos.up());
                    if (HOLLOW_LOGS.containsKey(above.getBlock())) return pos.toImmutable();
                }
            }
            return null;
        }

        @Override
        public boolean shouldContinue() {
            return targetLog != null && ticksTrying < 300;
        }

        @Override
        public void stop() {
            super.stop();
            this.targetLog = null;
            termite.setEatingTimer(0);
            termite.setEating(false);
            termite.getNavigation().stop();
        }
    }
}
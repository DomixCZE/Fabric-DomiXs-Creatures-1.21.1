package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.entity.ai.goal.entity.AntMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.goal.util.ColonyMember;
import net.domixcze.domixscreatures.entity.ai.goal.ReturnToColonyGoal;
import net.domixcze.domixscreatures.entity.ai.goal.StayNearColonyGoal;
import net.domixcze.domixscreatures.entity.client.ant.AntVariants;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
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
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
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
import java.util.Optional;

public class AntEntity extends AnimalEntity implements GeoEntity, ColonyMember {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<BlockPos> HOME_POS = DataTracker.registerData(AntEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(AntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_LEAVES = DataTracker.registerData(AntEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> EATING = DataTracker.registerData(AntEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<BlockState>> CARRIED_LEAF = DataTracker.registerData(AntEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);

    private int eatingTimer = 0;
    private int eatCooldown = 0;

    public AntEntity(EntityType<? extends AnimalEntity> entityType, World world) {
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
        this.goalSelector.add(1, new AntMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(1, new AntEatLeavesGoal(this));
        this.goalSelector.add(2, new ReturnToColonyGoal<>(this, 1.0));
        this.goalSelector.add(3, new StayNearColonyGoal<>(this, ModBlocks.ANT_NEST_BLOCK, 1.0, 8, 16));
        this.goalSelector.add(4, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(0, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, TermiteEntity.class, true));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(EATING, false);
        builder.add(HAS_LEAVES, false);
        builder.add(VARIANT, AntVariants.WORKER.getId());
        builder.add(HOME_POS, BlockPos.ORIGIN);
        builder.add(CARRIED_LEAF, Optional.empty());
    }

    public AntVariants getVariant() {
        return AntVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(AntVariants variant) {
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
            speedAttr.setBaseValue(variant == AntVariants.QUEEN ? 0.1D : 0.2D);
        }
    }

    public void setCarriedLeaf(BlockState state) {
        this.dataTracker.set(CARRIED_LEAF, Optional.ofNullable(state));
    }

    public ItemStack getCarriedLeafStack() {
        return this.dataTracker.get(CARRIED_LEAF)
                .map(state -> new ItemStack(state.getBlock()))
                .orElse(new ItemStack(Items.JUNGLE_LEAVES));
    }

    public void setHasLeaves(boolean hasLeaves) {
        this.dataTracker.set(HAS_LEAVES, hasLeaves);
    }

    public boolean hasLeaves() {
        return this.dataTracker.get(HAS_LEAVES);
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
        this.dataTracker.get(CARRIED_LEAF).ifPresent(state -> nbt.put("CarriedLeaf", NbtHelper.fromBlockState(state)));
        nbt.putBoolean("HasLeaves", hasLeaves());
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
        this.setVariant(AntVariants.byId(nbt.getInt("Variant")));
        this.eatingTimer = nbt.getInt("EatingTimer");
        setEating(nbt.getBoolean("IsEating"));
        this.eatCooldown = nbt.getInt("EatCooldown");
        setHasLeaves(nbt.getBoolean("HasLeaves"));
        if (nbt.contains("CarriedLeaf", 10)) {
            this.setCarriedLeaf(NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(net.minecraft.registry.RegistryKeys.BLOCK), nbt.getCompound("CarriedLeaf")));
        }
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
                this.setVariant(AntVariants.QUEEN);
            } else if (chance < 0.25) {
                this.setVariant(AntVariants.SOLDIER);
            } else {
                this.setVariant(AntVariants.WORKER);
            }
        }

        return entityData;
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);

        if (this.hasLeaves()) {
            ItemStack stack = this.getCarriedLeafStack();
            if (!stack.isEmpty()) {
                this.dropStack(stack);
            }
        }
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
        AnimationController<AntEntity> landController = new AnimationController<>(this, "land_controller", 5, this::landPredicate);
        landController.triggerableAnim("attack", RawAnimation.begin().then("animation.ant.attack", Animation.LoopType.PLAY_ONCE));
        controllers.add(landController);
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.ant.walk", Animation.LoopType.LOOP));
        } else if (this.isEating()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.ant.eat", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.ant.idle", Animation.LoopType.LOOP));
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

    public static class AntEatLeavesGoal extends Goal {
        private final AntEntity ant;
        private final World world;
        private BlockPos targetLeaf;
        private int ticksTrying;
        private final int EAT_TIME = 30; // 1.5 seconds

        public AntEatLeavesGoal(AntEntity ant) {
            this.ant = ant;
            this.world = ant.getWorld();
            this.setControls(EnumSet.of(Control.MOVE));
        }

        private boolean isValidLeaf(BlockPos pos) {
            BlockState state = world.getBlockState(pos);
            return state.isIn(BlockTags.LEAVES) && state.contains(LeavesBlock.PERSISTENT) && !state.get(LeavesBlock.PERSISTENT);
        }

        @Override
        public boolean canStart() {
            if (!this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) || !this.world.getGameRules().getBoolean(ModGameRules.ENABLE_ANT_LEAF_COLLECTING)) {
                return false;
            }

            if (ant.getHomePos() != BlockPos.ORIGIN) {
                double distSq = ant.squaredDistanceTo(ant.getHomePos().getX() + 0.5, ant.getY(), ant.getHomePos().getZ() + 0.5);
                if (distSq > 256) return false;
            }
            if (ant.getVariant() != AntVariants.WORKER || ant.hasLeaves() || ant.getEatCooldown() > 0) return false;
            if (targetLeaf != null || !ant.isOnGround()) return false;
            if (ant.getRandom().nextInt(15) != 0) return false;

            this.targetLeaf = findTargetBlock();
            return this.targetLeaf != null;
        }

        @Override
        public void start() {
            this.ticksTrying = 0;
            ant.setEatingTimer(0);
            ant.setEating(false);
            ant.getNavigation().startMovingTo(targetLeaf.getX() + 0.5, targetLeaf.getY(), targetLeaf.getZ() + 0.5, 1.0D);
        }

        @Override
        public void tick() {
            if (targetLeaf == null) return;

            if (!isValidLeaf(targetLeaf)) {
                this.stop();
                return;
            }

            if (ant.getEatingTimer() == 0) {
                ticksTrying++;
                double distSq = ant.squaredDistanceTo(targetLeaf.getX() + 0.5, ant.getY(), targetLeaf.getZ() + 0.5);

                if (distSq <= 1.44 || (ant.getNavigation().isIdle() && distSq <= 2.5)) {
                    ant.getNavigation().stop();

                    this.world.playSound(null, ant.getX(), ant.getY(), ant.getZ(),
                            ModSounds.TERMITE_EAT, ant.getSoundCategory(), 1.0F, 1.0F);

                    ant.setEating(true);
                    ant.setEatingTimer(1);
                }
                if (ticksTrying > 200) this.stop();
            } else {
                ant.setEatingTimer(ant.getEatingTimer() + 1);
                if (ant.getEatingTimer() >= EAT_TIME) {
                    ant.setCarriedLeaf(world.getBlockState(targetLeaf));
                    world.breakBlock(targetLeaf, false);

                    ant.setHasLeaves(true);
                    ant.setEatCooldown(1200);
                    this.stop();
                }
            }
        }

        protected BlockPos findTargetBlock() {
            BlockPos origin = ant.getBlockPos();
            for (BlockPos pos : BlockPos.iterate(origin.add(-8, -2, -8), origin.add(8, 2, 8))) {
                if (isValidLeaf(pos) && !world.getBlockState(pos.down()).isAir()) {
                    return pos.toImmutable();
                }
            }
            return null;
        }

        @Override
        public boolean shouldContinue() {
            return targetLeaf != null && ticksTrying < 300;
        }

        @Override
        public void stop() {
            super.stop();
            this.targetLeaf = null;
            ant.setEatingTimer(0);
            ant.setEating(false);
            ant.getNavigation().stop();
        }
    }
}

package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.client.hermit_crab.HermitCrabShapes;
import net.domixcze.domixscreatures.entity.client.hermit_crab.HermitCrabVariants;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;

public class HermitCrabEntity extends AnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(HermitCrabEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SHAPE = DataTracker.registerData(HermitCrabEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_HIDDEN = DataTracker.registerData(HermitCrabEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_DANCING = DataTracker.registerData(HermitCrabEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final Identifier HERMIT_CRAB_BARTERING_LOOT_TABLE_ID = Identifier.of(DomiXsCreatures.MOD_ID, "gameplay/hermit_crab_bartering");

    private int hideCooldown;
    private static final int HIDE_DISTANCE_SQ = 6 * 6;

    private int tradeCount;
    private int tradeCooldown;
    private static final int TRADE_COOLDOWN = 24000;

    public HermitCrabEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new HermitCrabDanceGoal(this));
        this.goalSelector.add(0, new HermitCrabHideGoal(this));
        this.goalSelector.add(1, new TemptGoal(this, 1.0, (stack) -> stack.isIn(ModTags.Items.HERMIT_CRAB_TEMPT), false));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION || spawnReason == SpawnReason.BREEDING) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.HERMIT_CRAB_SPAWNS_IN)) {
                this.setVariant(random.nextBoolean() ? HermitCrabVariants.BLUE : HermitCrabVariants.PINK);
            } else {
                this.setVariant(HermitCrabVariants.PINK);
            }
            this.setShape(random.nextBoolean() ? HermitCrabShapes.ROUND : HermitCrabShapes.POINTY);
        }

        return entityData;
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::Predicate));
    }

    private <T extends GeoAnimatable> PlayState Predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.hermit_crab.walk", Animation.LoopType.LOOP));
        } else if (this.isHidden()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.hermit_crab.hide", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else if (this.isDancing()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.hermit_crab.dance", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.hermit_crab.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, HermitCrabVariants.PINK.getId());
        builder.add(SHAPE, HermitCrabShapes.ROUND.getId());
        builder.add(IS_HIDDEN, false);
        builder.add(IS_DANCING, false);
    }

    public HermitCrabVariants getVariant() {
        return HermitCrabVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(HermitCrabVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    public HermitCrabShapes getShape() {
        return HermitCrabShapes.byId(this.dataTracker.get(SHAPE));
    }

    public void setShape(HermitCrabShapes shape) {
        this.dataTracker.set(SHAPE, shape.getId());
    }

    public boolean isHidden() {
        return this.dataTracker.get(IS_HIDDEN);
    }

    public void setHidden(boolean hidden) {
        this.dataTracker.set(IS_HIDDEN, hidden);
    }

    public boolean isDancing() {
        return this.dataTracker.get(IS_DANCING);
    }

    public void setDancing(boolean dancing) {
        this.dataTracker.set(IS_DANCING, dancing);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putInt("Shape", this.getShape().getId());
        nbt.putInt("TradeCount", this.tradeCount);
        nbt.putInt("TradeCooldown", this.tradeCooldown);
        nbt.putBoolean("IsHidden", this.isHidden());
        nbt.putInt("HideCooldown", this.hideCooldown);
        nbt.putBoolean("IsDancing", this.isDancing());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(HermitCrabVariants.byId(nbt.getInt("Variant")));
        this.setShape(HermitCrabShapes.byId(nbt.getInt("Shape")));
        this.tradeCount = nbt.getInt("TradeCount");
        this.tradeCooldown = nbt.getInt("TradeCooldown");
        this.setHidden(nbt.getBoolean("IsHidden"));
        this.hideCooldown = nbt.getInt("HideCooldown");
        this.setDancing(nbt.getBoolean("IsDancing"));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            return;
        }

        if (tradeCooldown > 0) {
            tradeCooldown--;
            if (tradeCooldown == 0) {
                tradeCount = 0;
            }
        }

        boolean runningPlayerNearby = this.isRunningPlayerNearby();

        if (runningPlayerNearby) {
            if (!this.isHidden()) {
                this.setHidden(true);
                this.setDancing(false);
            }

            if (this.isHidden()) {
                this.hideCooldown = 200;
            }

        } else {
            if (this.isHidden()) {
                if (this.hideCooldown <= 0) {
                    this.hideCooldown = 200;
                }

                this.hideCooldown--;
                if (this.hideCooldown == 0) {
                    this.setHidden(false);
                }
            } else {
                this.hideCooldown = 0;
            }
        }
    }

    private boolean isRunningPlayerNearby() {
        return !this.getWorld().getEntitiesByClass(PlayerEntity.class,
                this.getBoundingBox().expand(Math.sqrt(HermitCrabEntity.HIDE_DISTANCE_SQ)),
                (player) -> player.isAlive() && player.isSprinting()).isEmpty();
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.isHidden() || this.isDancing()) {
            return ActionResult.PASS;
        }

        if (itemStack.isIn(ModTags.Items.HERMIT_CRAB_TEMPT)) {
            if (!this.getWorld().isClient()) {
                ServerWorld serverWorld = (ServerWorld) this.getWorld();

                int boostAmount = 0;
                StatusEffectInstance tradeBoostInstance = player.getStatusEffect(ModEffects.OCEAN_BLESSING);
                if (tradeBoostInstance != null) {
                    // Each amplifier level adds 5 to the limit (amplifier 0 -> +5, 1 -> +10, etc.)
                    boostAmount = 5 * (tradeBoostInstance.getAmplifier() + 1);
                }

                if (tradeCooldown > 0) {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                            this.getX(), this.getBodyY(0.5D), this.getZ(),
                            5,
                            0D, 0D, 0D,
                            0.1D);
                    return ActionResult.SUCCESS;
                }

                if (this.tradeCount < ModConfig.INSTANCE.hermitCrabDailyTradeLimit + boostAmount) {
                    itemStack.decrement(1);
                    this.dropBarterLoot(player);
                    this.tradeCount++;

                    serverWorld.spawnParticles(ParticleTypes.HEART,
                            this.getX(), this.getBodyY(0.5D), this.getZ(),
                            1,
                            0.2D, 0.1D, 0.2D,
                            0.1D);

                    if (this.tradeCount >= ModConfig.INSTANCE.hermitCrabDailyTradeLimit + boostAmount) {
                        this.tradeCooldown = TRADE_COOLDOWN;
                    }
                } else {
                    serverWorld.spawnParticles(ParticleTypes.SMOKE,
                            this.getX(), this.getBodyY(0.5D), this.getZ(),
                            5,
                            0D, 0D, 0D,
                            0.1D);
                }
            }
            return ActionResult.success(this.getWorld().isClient());
        }

        return super.interactMob(player, hand);
    }

    private void dropBarterLoot(net.minecraft.entity.player.PlayerEntity player) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        RegistryKey<LootTable> lootTableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE, HERMIT_CRAB_BARTERING_LOOT_TABLE_ID);
        LootTable lootTable = serverWorld.getServer().getReloadableRegistries().getLootTable(lootTableKey);

        LootContextParameterSet lootContextParameterSet = (new LootContextParameterSet.Builder(serverWorld))
                .add(LootContextParameters.THIS_ENTITY, this)
                .luck(player.getLuck())
                .build(LootContextTypes.BARTER);

        List<ItemStack> generatedLoot = lootTable.generateLoot(lootContextParameterSet);

        for (ItemStack itemStack : generatedLoot) {
            this.dropStack(itemStack);
        }
    }

    private static class HermitCrabHideGoal extends Goal {
        private final HermitCrabEntity crab;

        public HermitCrabHideGoal(HermitCrabEntity crab) {
            this.crab = crab;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return !this.crab.getWorld().isClient() &&
                    (this.crab.isRunningPlayerNearby() || (this.crab.isHidden() && this.crab.hideCooldown > 0));
        }

        @Override
        public boolean shouldContinue() {
            return this.crab.isHidden() || this.crab.isRunningPlayerNearby();
        }

        @Override
        public void start() {
            this.crab.setHidden(true);
            this.crab.getNavigation().stop();
            this.crab.getLookControl().tick();
        }

        @Override
        public void stop() {
            this.crab.setHidden(false);
        }
    }

    private static class HermitCrabDanceGoal extends Goal {
        private final HermitCrabEntity crab;
        private final int searchRadius = 8;

        public HermitCrabDanceGoal(HermitCrabEntity crab) {
            this.crab = crab;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return !this.crab.isHidden() && isJukeboxPlayingNearby();
        }

        @Override
        public boolean shouldContinue() {
            return !this.crab.isHidden() && isJukeboxPlayingNearby();
        }

        @Override
        public void start() {
            this.crab.setDancing(true);
            this.crab.getNavigation().stop();
            this.crab.getLookControl().tick();
        }

        @Override
        public void stop() {
            this.crab.setDancing(false);
        }

        private boolean isJukeboxPlayingNearby() {
            BlockPos entityPos = this.crab.getBlockPos();
            for (int x = -searchRadius; x <= searchRadius; x++) {
                for (int y = -searchRadius; y <= searchRadius; y++) {
                    for (int z = -searchRadius; z <= searchRadius; z++) {
                        BlockPos currentPos = entityPos.add(x, y, z);
                        BlockState blockState = this.crab.getWorld().getBlockState(currentPos);

                        if (blockState.isOf(Blocks.JUKEBOX)) {
                            BlockEntity blockEntity = this.crab.getWorld().getBlockEntity(currentPos);

                            if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                                if (jukeboxBlockEntity.getManager().isPlaying()) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }
    }
}
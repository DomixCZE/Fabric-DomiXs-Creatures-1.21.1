package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.block.custom.MolehillBlock;
import net.domixcze.domixscreatures.block.entity.MolehillBlockEntity;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.MoleMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.mole.MoleVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
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

public class MoleEntity extends AnimalEntity implements GeoEntity, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(BeaverEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private boolean insideHill = false;
    private BlockPos currentHillPos;
    private int cooldown = 0;

    public MoleEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(1, new MoleMeleeAttackGoal(this, 1.0, true, 1.5));
        this.goalSelector.add(2, new MoleEntityAvoidanceGoal(this));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, WormEntity.class, true));
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.WORM);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAS_SNOW_LAYER, false);
        this.dataTracker.startTracking(VARIANT, MoleVariants.BLACK.getId());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("InsideHill", this.insideHill);
        if (this.currentHillPos != null) {
            nbt.putLong("CurrentHillPos", this.currentHillPos.asLong());
        }
        nbt.putInt("Cooldown", this.cooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setVariant(MoleVariants.byId(nbt.getInt("Variant")));
        this.insideHill = nbt.getBoolean("InsideHill");
        if (nbt.contains("CurrentHillPos")) {
            this.currentHillPos = BlockPos.fromLong(nbt.getLong("CurrentHillPos"));
        }
        if (nbt.contains("Cooldown")) {
            this.cooldown = nbt.getInt("Cooldown");
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

    public MoleVariants getVariant() {
        return MoleVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(MoleVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(MoleVariants.ALBINO);
        } else {
            this.setVariant(MoleVariants.BLACK);
        }
        return entityData;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        MoleEntity baby = (MoleEntity) ModEntities.MOLE.create(world);
        if (baby != null) {
            MoleEntity parent1 = this;
            MoleEntity parent2 = (MoleEntity) entity;

            if ((parent1.getVariant() == MoleVariants.ALBINO) && (parent2.getVariant() == MoleVariants.ALBINO)) {
                baby.setVariant(MoleVariants.ALBINO);
            } else if (parent1.getVariant() == MoleVariants.ALBINO || parent2.getVariant() == MoleVariants.ALBINO) {
                double chance = 0.25;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(MoleVariants.ALBINO);
                } else {
                    baby.setVariant(MoleVariants.BLACK);
                }
            } else {
                double chance = 0.01;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(MoleVariants.ALBINO);
                } else {
                    baby.setVariant(MoleVariants.BLACK);
                }
            }
        }
        return baby;
    }

    public boolean isInsideHill() {
        return this.insideHill;
    }

    public void enterHill(MolehillBlockEntity hill) {
        this.insideHill = true;
        this.currentHillPos = hill.getPos();
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_GRAVEL_BREAK, SoundCategory.BLOCKS, 1.0f, 0.8f + random.nextFloat() * 0.4f);
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public void tick() {
        super.tick();
        if (cooldown > 0) {
            cooldown--;
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::landPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.mole.walk", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.mole.idle", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    public static class MoleEntityAvoidanceGoal extends Goal {
        private final MoleEntity mole;
        private final World world;
        private static final double DETECTION_RADIUS = 5.0;

        public MoleEntityAvoidanceGoal(MoleEntity mole) {
            this.mole = mole;
            this.world = mole.getWorld();
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            boolean otherAnimalThreatNearby = !world.getEntitiesByClass(AnimalEntity.class,
                            mole.getBoundingBox().expand(DETECTION_RADIUS),
                            entity -> entity != mole && !(entity instanceof MoleEntity) && !(entity instanceof WormEntity))
                    .isEmpty();

            boolean playerThreatNearby = !world.getEntitiesByClass(PlayerEntity.class,
                            mole.getBoundingBox().expand(DETECTION_RADIUS),
                            player -> !player.isSpectator() && !player.isCreative())
                    .isEmpty();

            boolean threatNearby = otherAnimalThreatNearby || playerThreatNearby;

            return threatNearby && !mole.isInsideHill() && mole.getCooldown() <= 0;
        }

        @Override
        public void tick() {
            BlockPos currentPos = mole.getBlockPos();
            Optional<MolehillBlockEntity> nearbyHill = MolehillBlockEntity.findNearbyUnoccupiedHill((ServerWorld) world, currentPos);

            if (nearbyHill.isPresent()) {
                mole.getNavigation().startMovingTo(
                        nearbyHill.get().getPos().getX(),
                        nearbyHill.get().getPos().getY(),
                        nearbyHill.get().getPos().getZ(),
                        1.0D
                );

                if (mole.getBlockPos().isWithinDistance(nearbyHill.get().getPos(), 2.0)) {
                    mole.enterHill(nearbyHill.get());
                    world.setBlockState(nearbyHill.get().getPos(),
                            world.getBlockState(nearbyHill.get().getPos()).with(MolehillBlock.OCCUPIED, true));
                    nearbyHill.get().storeMoleData(mole.writeNbt(new NbtCompound()));
                    mole.discard();
                }
            } else {
                createNewMolehillIfPossible(currentPos);
            }
        }

        private void createNewMolehillIfPossible(BlockPos currentPos) {
            BlockState currentState = world.getBlockState(currentPos);
            BlockPos belowPos = currentPos.down();
            BlockState belowState = world.getBlockState(belowPos);

            if (currentState.isOf(ModBlocks.MOLEHILL_BLOCK)) {
                return;
            }

            if (mole.isOnGround()
                    && !belowState.isOf(ModBlocks.MOLEHILL_BLOCK)
                    && (belowState.isOf(Blocks.GRASS_BLOCK) || belowState.isOf(Blocks.DIRT))) {

                world.setBlockState(currentPos, ModBlocks.MOLEHILL_BLOCK.getDefaultState());

                MolehillBlockEntity moleHillEntity = (MolehillBlockEntity) world.getBlockEntity(currentPos);
                if (moleHillEntity != null) {
                    mole.enterHill(moleHillEntity);
                    world.setBlockState(currentPos, world.getBlockState(currentPos).with(MolehillBlock.OCCUPIED, true));
                    moleHillEntity.storeMoleData(mole.writeNbt(new NbtCompound()));
                    mole.discard();
                }
            }
        }
    }
}
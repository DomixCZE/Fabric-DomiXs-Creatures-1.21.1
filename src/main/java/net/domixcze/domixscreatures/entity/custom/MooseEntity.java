package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.MooseMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.moose.MooseAntlerSize;
import net.domixcze.domixscreatures.entity.client.moose.MooseVariants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MooseEntity extends AnimalEntity implements GeoEntity, SnowLayerable, Sleepy {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(MooseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(MooseEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(MooseEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ANTLER_SIZE = DataTracker.registerData(MooseEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public MooseEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.setStepHeight(1.2f);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, true, false, true, false, 5.0, 500, 700, true, false, true, true));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new MooseMeleeAttackGoal(this, 1.0, true, 2));
        this.goalSelector.add(3, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, (new RevengeGoal(this)));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAS_SNOW_LAYER, false);
        this.dataTracker.startTracking(SLEEPING, false);
        this.dataTracker.startTracking(VARIANT, MooseVariants.BROWN.ordinal());
        this.dataTracker.startTracking(ANTLER_SIZE, MooseAntlerSize.NONE.ordinal());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_moose.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_moose.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.moose.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.moose.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_moose.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.moose.sleep", Animation.LoopType.LOOP));
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
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(MooseVariants.ALBINO);
        } else {
            this.setVariant(MooseVariants.BROWN);
        }

        if (!this.isBaby()) {
            if (world.getRandom().nextFloat() < 0.2f) {
                this.setAntlerSize(MooseAntlerSize.MEDIUM);
            } else {
                this.setAntlerSize(MooseAntlerSize.NONE);
            }
        }

        return entityData;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.8F, 1.4F);
        }
        return super.getDimensions(pose);
    }

    @Override
    protected void onGrowUp() {
        super.onGrowUp();

        if (!this.getWorld().isClient()) {
            Random random = this.random;

            if (random.nextFloat() < 0.5f) {
                this.setAntlerSize(MooseAntlerSize.MEDIUM); //50%
            } else {
                this.setAntlerSize(MooseAntlerSize.NONE); //50%
            }
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        MooseEntity baby = (MooseEntity) ModEntities.MOOSE.create(world);
        if (baby != null) {
            MooseEntity parent1 = this;
            MooseEntity parent2 = (MooseEntity) entity;

            if ((parent1.getVariant() == MooseVariants.ALBINO) && (parent2.getVariant() == MooseVariants.ALBINO)) {
                baby.setVariant(MooseVariants.ALBINO);
            } else if (parent1.getVariant() == MooseVariants.ALBINO || parent2.getVariant() == MooseVariants.ALBINO) {
                double chance = 0.25;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(MooseVariants.ALBINO);
                } else {
                    baby.setVariant(MooseVariants.BROWN);
                }
            } else {
                double chance = 0.01;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(MooseVariants.ALBINO);
                } else {
                    baby.setVariant(MooseVariants.BROWN);
                }
            }
            baby.setAntlerSize(MooseAntlerSize.NONE);
        }
        return baby;
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.APPLE);
    }

    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other == this) {
            return false;
        } else if (!(other instanceof MooseEntity)) {
            return false;
        } else {
            MooseEntity otherMoose = (MooseEntity) other;
            boolean thisHasAntlers = this.getAntlerSize() != MooseAntlerSize.NONE;
            boolean otherHasAntlers = otherMoose.getAntlerSize() != MooseAntlerSize.NONE;

            return this.isInLove() && other.isInLove() && (thisHasAntlers ^ otherHasAntlers);
        }
    }

    @Override
    public void tick() {
        super.tick();
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
                    int count = 5 + this.getWorld().random.nextInt(4);
                    this.dropStack(new ItemStack(Items.SNOWBALL, count));
                }

                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putInt("AntlerSize", this.getAntlerSize().ordinal());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setVariant(MooseVariants.values()[nbt.getInt("Variant")]);
        this.setAntlerSize(MooseAntlerSize.values()[nbt.getInt("AntlerSize")]);
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

    public MooseVariants getVariant() {
        return MooseVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(MooseVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    public MooseAntlerSize getAntlerSize() {
        return MooseAntlerSize.values()[this.dataTracker.get(ANTLER_SIZE)];
    }

    public void setAntlerSize(MooseAntlerSize size) {
        this.dataTracker.set(ANTLER_SIZE, size.ordinal());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_HORSE_STEP, 0.15F, 1.0F);
    }
}
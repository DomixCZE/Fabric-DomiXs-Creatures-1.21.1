package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.ai.TigerMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.client.tiger.TigerVariants;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
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

import java.util.UUID;

public class TigerEntity  extends TameableEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(TigerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(TigerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> AMULET_USED = DataTracker.registerData(TigerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(TigerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(TigerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public TigerEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, true, false, true, false, 3.0, 500, 700, true, false, true, true));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new TigerMeleeAttackGoal(this, 1.0, true, 1.7));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(5, new LookAroundGoal(this));

        this.targetSelector.add(1, new AttackWithOwnerGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(HAS_SNOW_LAYER, false);
        this.dataTracker.startTracking(VARIANT, TigerVariants.NORMAL.ordinal());
        this.dataTracker.startTracking(AMULET_USED, false);
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(SLEEPING, false);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(TigerVariants.ALBINO);
        } else {
            this.setVariant(TigerVariants.NORMAL);
        }
        return entityData;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        EntityAttributeInstance movementSpeed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);

        if (movementSpeed != null) {
            if (this.isBaby()) {
                // Baby speed values
                if (this.isOnGround()) {
                    movementSpeed.setBaseValue(0.2f);
                } else if (this.isTouchingWater()) {
                    movementSpeed.setBaseValue(0.6f);
                }
            } else {
                // Adult speed values
                if (this.isOnGround()) {
                    movementSpeed.setBaseValue(0.2f);
                } else if (this.isTouchingWater()) {
                    movementSpeed.setBaseValue(0.8f);
                }
            }
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        TigerEntity baby = (TigerEntity) ModEntities.TIGER.create(world);
        if (baby != null) {
            TigerEntity parent1 = this;
            TigerEntity parent2 = (TigerEntity) entity;

            if ((parent1.getVariant() == TigerVariants.ALBINO || parent1.getVariant() == TigerVariants.ALBINO_DREAM) &&
                    (parent2.getVariant() == TigerVariants.ALBINO || parent2.getVariant() == TigerVariants.ALBINO_DREAM)) {
                baby.setVariant(TigerVariants.ALBINO); // 100% if both parents are albino (or albino dream)
            } else if (parent1.getVariant() == TigerVariants.ALBINO || parent1.getVariant() == TigerVariants.ALBINO_DREAM || parent2.getVariant() == TigerVariants.ALBINO || parent2.getVariant() == TigerVariants.ALBINO_DREAM) {
                double chance = 0.25; // 25% if one parent is albino (or albino dream)

                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(TigerVariants.ALBINO);
                } else {
                    baby.setVariant(TigerVariants.NORMAL);
                }
            } else{
                double chance = 0.01; // 1% chance if neither parent is albino (or albino dream)
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(TigerVariants.ALBINO);
                } else {
                    baby.setVariant(TigerVariants.NORMAL);
                }
            }
        }
        return baby;
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

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSit(PlayerEntity player, boolean sitting) {
        if (player == getOwner()) {
            this.dataTracker.set(SITTING, sitting);
            super.setSitting(sitting);
        }
    }

    public TigerVariants getVariant() {
        return TigerVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(TigerVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    public boolean hasAmulet() {
        return this.dataTracker.get(AMULET_USED);
    }

    public void setAmuletUsed(boolean used) {
        this.dataTracker.set(AMULET_USED, used);
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
    public void tick() {
        super.tick();

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

        if (!this.getWorld().isClient() && this.isTamed() && this.getOwner() instanceof ServerPlayerEntity player) {
            if (this.getWorld().isNight()) {
                int sleeplessNights = getSleeplessNights(player);

                if (sleeplessNights >= 3 && this.hasAmulet()) {
                    if (this.getVariant() == TigerVariants.NORMAL) {
                        this.setVariant(TigerVariants.DREAM);
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F, 1.0F);
                        if (this.getWorld() instanceof ServerWorld serverWorld) {
                            spawnTransformationParticles(serverWorld);
                        }
                        applyStrengthEffect(sleeplessNights);
                    } else if (this.getVariant() == TigerVariants.ALBINO) {
                        this.setVariant(TigerVariants.ALBINO_DREAM);
                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F, 1.0F);
                        if (this.getWorld() instanceof ServerWorld serverWorld) {
                            spawnTransformationParticles(serverWorld);
                        }
                        applyStrengthEffect(sleeplessNights);
                    }
                }
            } else if (this.getWorld().isDay() && (this.getVariant() == TigerVariants.DREAM || this.getVariant() == TigerVariants.ALBINO_DREAM)) {
                if (this.getVariant() == TigerVariants.DREAM) {
                    this.setVariant(TigerVariants.NORMAL);
                } else {
                    this.setVariant(TigerVariants.ALBINO);
                }
                this.removeStatusEffect(StatusEffects.STRENGTH);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F, 1.0F);
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    spawnTransformationParticles(serverWorld);
                }
            }
        }
    }

    private void spawnTransformationParticles(ServerWorld serverWorld) {
        Vec3d source = this.getPos().add(0.0, this.getHeight() / 2.0, 0.0);
        int particleCount = 40;

        for (int i = 0; i < particleCount; i++) {
            double xOffset = (this.random.nextDouble() - 0.5) * this.getWidth();
            double yOffset = (this.random.nextDouble() - 0.5) * this.getHeight();
            double zOffset = (this.random.nextDouble() - 0.5) * this.getWidth();
            Vec3d particlePos = source.add(xOffset, yOffset, zOffset);
            serverWorld.spawnParticles(ModParticles.INK, particlePos.x, particlePos.y, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void applyStrengthEffect(int sleeplessNights) {
        int strengthLevel = Math.min(sleeplessNights - 3, 2);
        if(strengthLevel >= 0) {
            if (!this.hasStatusEffect(StatusEffects.STRENGTH)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 205, strengthLevel), this);
            }
        }
    }

    private int getSleeplessNights(ServerPlayerEntity player) {
        return MathHelper.floor(player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)) / 24000f);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putBoolean("AmuletUsed", this.hasAmulet());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setVariant(TigerVariants.values()[nbt.getInt("Variant")]);
        this.setAmuletUsed(nbt.getBoolean("AmuletUsed"));
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.6F, 0.5F);
        }
        return super.getDimensions(pose);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }

        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.walk", Animation.LoopType.LOOP));
            } else if (this.isSitting() && !this.isSleeping()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.idle", Animation.LoopType.LOOP));
            }
        } else if (this.getVariant() == TigerVariants.DREAM || this.getVariant() == TigerVariants.ALBINO_DREAM) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.walk", Animation.LoopType.LOOP));
            } else if (this.isSitting() && !this.isSleeping()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.walk", Animation.LoopType.LOOP));
            } else if (this.isSitting() && !this.isSleeping()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.sit", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.sleep", Animation.LoopType.LOOP));
            } else if (this.getVariant() == TigerVariants.DREAM || this.getVariant() == TigerVariants.ALBINO_DREAM) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.sleep", Animation.LoopType.LOOP));
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
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_tiger.idle_swim", Animation.LoopType.LOOP));
            }
        } else if (this.getVariant() == TigerVariants.DREAM || this.getVariant() == TigerVariants.ALBINO_DREAM) {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.dream_tiger.idle_swim", Animation.LoopType.LOOP));
            }
        } else {
            if (state.isMoving()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.swim", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.tiger.idle_swim", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(Items.PORKCHOP);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        if ((itemstack.getItem() == Items.BEEF) && !this.isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                if (this.random.nextInt(15) == 0) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        } else if (itemstack.getItem() == ModItems.NIGHTMARE_AMULET && this.isTamed() && !this.getWorld().isClient() && !this.hasAmulet() && !this.isBaby()) {
            this.setAmuletUsed(true);
            if (!player.getAbilities().creativeMode) {
                itemstack.decrement(1);
            }
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F, 1.0F);
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                Vec3d source = this.getPos().add(0.0, this.getHeight() / 2.0, 0.0);
                int particleCount = 40;

                for (int i = 0; i < particleCount; i++) {
                    double xOffset = (this.random.nextDouble() - 0.5) * this.getWidth();
                    double yOffset = (this.random.nextDouble() - 0.5) * this.getHeight();
                    double zOffset = (this.random.nextDouble() - 0.5) * this.getWidth();
                    Vec3d particlePos = source.add(xOffset, yOffset, zOffset);
                    serverWorld.spawnParticles(ModParticles.INK, particlePos.x, particlePos.y, particlePos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
            return ActionResult.SUCCESS;
        }

        if (isTamed() && !this.getWorld().isClient()) {
            if (isTamed() && hand == Hand.MAIN_HAND && itemstack.isEmpty()) {
                setSit(player, !isSitting());

                Text entityName = this.getDisplayName();
                String action = isSitting() ? "is Sitting" : "is Following";

                Text message = Text.literal(entityName.getString() + " " + action + ".")
                        .styled(style -> style.withColor(Formatting.GREEN));
                player.sendMessage(message, true);

                return ActionResult.PASS;
            }
        }

        if (itemstack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemstack.damage(1, player, (p) -> p.sendToolBreakStatus(hand));
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

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    //SOUNDS
    /*@Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return ModSounds.CROCODILE_AMBIENT;
    }*/

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.TIGER_HURT;
    }

    /*@Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CROCODILE_DEATH;
    }*/
}

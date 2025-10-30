package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.BabyFollowParentGoal;
import net.domixcze.domixscreatures.entity.ai.SleepGoal;
import net.domixcze.domixscreatures.entity.ai.Sleepy;
import net.domixcze.domixscreatures.entity.ai.SnowLayerable;
import net.domixcze.domixscreatures.entity.client.porcupine.PorcupineVariants;
import net.domixcze.domixscreatures.util.ModTags;
import net.domixcze.domixscreatures.util.SnowLayerUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PorcupineEntity  extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> QUILLS = DataTracker.registerData(PorcupineEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(PorcupineEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(PorcupineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(PorcupineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    private int quillRegenCooldown = 0;

    private static final int MAX_QUILL_CLUSTERS = 5;
    private static final int REGEN_COOLDOWN_TICKS = 20 * 60; // 60s per quill regrow

    public PorcupineEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2);
    }

    @Override
    public boolean canBeLeashed() {
        return !this.isSleeping();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.PORCUPINE_FOR_BREEDING);
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

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, 100,false, true, true, true, 7.0, 500, 700, true, false, true, true,1));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(2, new BabyFollowParentGoal(this, 0.8));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(QUILLS, MAX_QUILL_CLUSTERS);
        builder.add(SLEEPING, false);
        builder.add(VARIANT, PorcupineVariants.NORMAL.getId());
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength * 0.1, x, z);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (ModConfig.INSTANCE.enablePorcupineQuillAttack && !this.getWorld().isClient() && !this.isDead() && !this.isBaby() && getQuillsAvailable() > 0) {
            boolean wasHurt = super.damage(source, amount);
            LivingEntity attacker = source.getAttacker() instanceof LivingEntity ? (LivingEntity) source.getAttacker() : null;
            if (wasHurt && attacker != null) {
                shootQuills(attacker);
                setQuillsAvailable(getQuillsAvailable() - 1);
                quillRegenCooldown = 0;
            }
            return wasHurt;
        }
        return super.damage(source, amount);
    }

    private void shootQuills(LivingEntity attacker) {
        if (attacker != null) {
            World world = this.getWorld();
            Random random = this.getRandom();
            int numberOfQuills = random.nextInt(3) + 3; // Shoot 3-5 quills per hit

            for (int i = 0; i < numberOfQuills; ++i) {
                QuillProjectileEntity quill = new QuillProjectileEntity(world, this.getX(), this.getEyeY() - 0.1, this.getZ());

                Vec3d backwardVec = this.getRotationVec(1.0F).normalize().multiply(-0.4);

                double upwardVelocity = 0.5 + random.nextDouble() * 0.15;

                double spreadXZ = (random.nextDouble() - 0.5) * 0.25;

                Vec3d velocity = new Vec3d(
                        (backwardVec.x + spreadXZ) * 0.3,
                        upwardVelocity * 0.9,
                        (backwardVec.z + spreadXZ) * 0.3
                );

                quill.setVelocity(velocity);
                quill.setOwner(this);
                world.spawnEntity(quill);
            }
            this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F);
        }
    }

    public int getQuillsAvailable() {
        return this.dataTracker.get(QUILLS);
    }

    public void setQuillsAvailable(int amount) {
        this.dataTracker.set(QUILLS, amount);
    }

    @Override
    public void writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putInt("QuillRegenCooldown", quillRegenCooldown);
        nbt.putInt("QuillsAvailable", this.getQuillsAvailable());
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
    }

    @Override
    public void readCustomDataFromNbt(net.minecraft.nbt.NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(PorcupineVariants.byId(nbt.getInt("Variant")));
        quillRegenCooldown = nbt.getInt("QuillRegenCooldown");
        this.setQuillsAvailable(nbt.getInt("QuillsAvailable"));
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
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

    public PorcupineVariants getVariant() {
        return PorcupineVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(PorcupineVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
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
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.4F, 0.4F);
        }
        return this.getType().getDimensions();
    }

    @Override
    public void tick() {
        super.tick();

        SnowLayerUtil.handleSnowLayerTick(this, this);

        if (!this.getWorld().isClient && !this.isBaby()) {
            if (getQuillsAvailable() < MAX_QUILL_CLUSTERS) {
                quillRegenCooldown++;
                if (quillRegenCooldown >= REGEN_COOLDOWN_TICKS) {
                    setQuillsAvailable(getQuillsAvailable() + 1);
                    quillRegenCooldown = 0;
                }
            }
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (world.getRandom().nextDouble() < 0.05) {
            this.setVariant(PorcupineVariants.ALBINO);
        } else {
            this.setVariant(PorcupineVariants.NORMAL);
        }

        if (this.isBaby()) {
            this.setQuillsAvailable(0);
        } else {
            this.setQuillsAvailable(MAX_QUILL_CLUSTERS);
        }

        this.quillRegenCooldown = 0;
        return entityData;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        PorcupineEntity baby = (PorcupineEntity) ModEntities.PORCUPINE.create(world);
        if (baby != null) {
            PorcupineEntity parent1 = this;
            PorcupineEntity parent2 = (PorcupineEntity) entity;

            if ((parent1.getVariant() == PorcupineVariants.ALBINO) && (parent2.getVariant() == PorcupineVariants.ALBINO)) {
                baby.setVariant(PorcupineVariants.ALBINO);
            } else if (parent1.getVariant() == PorcupineVariants.ALBINO || parent2.getVariant() == PorcupineVariants.ALBINO) {
                double chance = 0.25;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(PorcupineVariants.ALBINO);
                } else {
                    baby.setVariant(PorcupineVariants.NORMAL);
                }
            } else {
                double chance = 0.01;
                if (world.getRandom().nextDouble() < chance) {
                    baby.setVariant(PorcupineVariants.ALBINO);
                } else {
                    baby.setVariant(PorcupineVariants.NORMAL);
                }
            }
        }
        return baby;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
        controllers.add(new AnimationController<>(this, "sleep_controller", 5, this::sleepPredicate));
    }

    private <P extends GeoAnimatable> PlayState landPredicate(AnimationState<P> state) {
        if (this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_porcupine.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_porcupine.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.porcupine.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.porcupine.idle", Animation.LoopType.LOOP));
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_porcupine.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.porcupine.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_porcupine.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.porcupine.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
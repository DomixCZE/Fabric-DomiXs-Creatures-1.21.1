package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.Beachable;
import net.domixcze.domixscreatures.entity.ai.BeachedGoal;
import net.domixcze.domixscreatures.entity.ai.EelMeleeAttackGoal;
import net.domixcze.domixscreatures.entity.client.eel.EelVariants;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;


public class EelEntity extends WaterCreatureEntity implements GeoEntity, Beachable {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(EelEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> CHARGED = DataTracker.registerData(EelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> BEACHED = DataTracker.registerData(EelEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int particleCooldown = 0;
    public int attackCooldown;
    public static final int ATTACK_COOLDOWN = 1000;

    public EelEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 70, 5, 0.01F, 0.05F, true);
        this.lookControl = new YawAdjustingLookControl(this, 8);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return WaterCreatureEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 2.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new BeachedGoal(this, this));
        this.goalSelector.add(1, new EelMeleeAttackGoal(this, 1.0, true, 2));
        this.goalSelector.add(2, new SwimAroundGoal(this, 0.8, 12));
        this.goalSelector.add(3, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, SalmonEntity.class, true));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, CodEntity.class, true));
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.40F;
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);

        setAttackCooldown(ATTACK_COOLDOWN);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());

            if (biomeEntry.isIn(ModTags.Biomes.SPAWNS_YELLOW_EEL)) {
                this.setVariant(random.nextBoolean() ? EelVariants.YELLOW : EelVariants.GREEN);
            } else if (biomeEntry.isIn(ModTags.Biomes.SPAWNS_ABYSS_EEL)) {
                this.setVariant(random.nextFloat() < 0.1f ? EelVariants.ABYSS : EelVariants.GREEN);
            } else {
                this.setVariant(EelVariants.GREEN);
            }
        }

        return entityData;
    }

    public EelVariants getVariant() {
        return EelVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(EelVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    @Override
    public void tick() {
        super.tick();
        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown <= 0) {
                setCharged(true);
            }
        }

        if (isCharged() && getWorld().isClient) {
            if (particleCooldown <= 0) {
                spawnElectricParticles();
                particleCooldown = 5;
            } else {
                particleCooldown--;
            }
        }
    }

    private void spawnElectricParticles() {
        for (int i = 0; i < 2; i++) {
            double offsetX = (random.nextDouble() * 0.7) - 0.35;
            double offsetY = (random.nextDouble() * 0.3) + 0.2;
            double offsetZ = (random.nextDouble() * 0.7) - 0.35;

            getWorld().addParticle(ParticleTypes.ELECTRIC_SPARK,
                    getX() + offsetX,
                    getY() + offsetY,
                    getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, EelVariants.GREEN.ordinal());
        this.dataTracker.startTracking(CHARGED, false);
        this.dataTracker.startTracking(BEACHED, false);
    }

    public boolean isBeached() {
        return this.dataTracker.get(BEACHED);
    }

    public void setBeached(boolean beached) {
        this.dataTracker.set(BEACHED, beached);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putBoolean("Charged", this.isCharged());
        nbt.putInt("AttackCooldown", this.attackCooldown);
        nbt.putBoolean("Beached", this.isBeached());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(EelVariants.values()[nbt.getInt("Variant")]);
        this.setBeached(nbt.getBoolean("Beached"));
        this.setCharged(nbt.getBoolean("Charged"));
        if (nbt.contains("AttackCooldown")) {
            this.attackCooldown = nbt.getInt("AttackCooldown");
        } else {
            setAttackCooldown(ATTACK_COOLDOWN);
        }
    }

    public int getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
        if (attackCooldown > 0) {
            setCharged(false);
        }
    }

    public boolean isCharged() {
        return this.dataTracker.get(CHARGED);
    }

    public void setCharged(boolean charged) {
        this.dataTracker.set(CHARGED, charged);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller",5 , this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.eel.swim", Animation.LoopType.LOOP));
        } else if (isBeached()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.eel.beached", Animation.LoopType.HOLD_ON_LAST_FRAME));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.eel.idle_swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }
}
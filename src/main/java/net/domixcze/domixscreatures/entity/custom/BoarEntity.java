package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.ai.*;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Optional;

public class BoarEntity extends AnimalEntity implements GeoEntity, Sleepy, SnowLayerable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(BoarEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_DIGGING = DataTracker.registerData(BoarEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> SLEEPING = DataTracker.registerData(BoarEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    private int diggingCooldown = 0;
    private final int MAX_DIGGING_COOLDOWN = 6000;
    private int diggingTimer = 0;
    private final int MAX_DIGGING_TIMER = 200;

    public BoarEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SleepGoal(this, this, true, false, true, false, 5.0, 500, 700, true, false, true, true));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new BoarMeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new BoarDigGoal(this));
        this.goalSelector.add(4, new BabyFollowParentGoal(this, 1.0));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ProtectBabiesGoal<>(this, BoarEntity.class, 8.0));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(IS_DIGGING, false);
        builder.add(SLEEPING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsDigging", this.isDigging());
        nbt.putInt("DiggingCooldown", this.diggingCooldown);
        nbt.putInt("DiggingTimer", this.diggingTimer);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
        this.setDigging(nbt.getBoolean("IsDigging"));
        this.diggingCooldown = nbt.getInt("DiggingCooldown");
        this.diggingTimer = nbt.getInt("DiggingTimer");
        this.setSleeping(nbt.getBoolean("Sleeping"));
    }

    public boolean isBeingSnowedOn() {
        BlockPos blockPos = this.getBlockPos();
        return this.getWorld().isRaining() && this.isInSnowyBiome() && (this.hasSnow(blockPos) || this.hasSnow(BlockPos.ofFloored(blockPos.getX(), this.getBoundingBox().maxY, blockPos.getZ())));
    }

    public boolean hasSnow(BlockPos pos) {
        if (!this.getWorld().isRaining()) {
            return false;
        } else if (!this.getWorld().isSkyVisible(pos)) {
            return false;
        } else if (this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            Biome biome = this.getWorld().getBiome(pos).value();
            return biome.getPrecipitation(pos) == Biome.Precipitation.SNOW;
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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isIn(ItemTags.SHOVELS)) {
            if (this.hasSnowLayer()) {
                this.setHasSnowLayer(false);
                snowMeltTimer = 0;

                if (!player.isCreative()) {
                    itemStack.damage(1, player, EquipmentSlot.MAINHAND);
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
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        if (this.isBaby()) {
            return EntityDimensions.fixed(0.5F, 0.5F);
        }
        return this.getType().getDimensions();
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
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_boar.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_boar.idle", Animation.LoopType.LOOP));
            }
        } else {
            if (this.isDigging()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.boar.dig", Animation.LoopType.LOOP));
            } else if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.boar.walk", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.boar.idle", Animation.LoopType.LOOP));;
            }
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        }
        if (this.isBaby()) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.baby_boar.swim", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.boar.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isSleeping()) {
            if (this.isBaby()) {
                state.getController().setAnimation(RawAnimation.begin().then("animation.baby_boar.sleep", Animation.LoopType.LOOP));
            } else {
                state.getController().setAnimation(RawAnimation.begin().then("animation.boar.sleep", Animation.LoopType.LOOP));
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.BOAR.create(world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.BOAR_FOR_BREEDING);
    }

    public boolean isDigging() {
        return this.dataTracker.get(IS_DIGGING);
    }

    public void setDigging(boolean isDigging) {
        this.dataTracker.set(IS_DIGGING, isDigging);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.hasSnowLayer() && this.isBeingSnowedOn()) {
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

        if (!this.getWorld().isClient) {
            if (!this.isDigging() && this.diggingCooldown > 0) {
                this.diggingCooldown--;
            }

            if (this.isDigging()) {
                this.diggingTimer++;

                if (this.diggingTimer >= MAX_DIGGING_TIMER) {
                    setDigging(false);
                    this.diggingCooldown = MAX_DIGGING_COOLDOWN;
                    this.diggingTimer = 0;
                    digAndDrop(this.getWorld(), this.getBlockPos().down(), this.getRandom());
                }
            } else {
                if (this.diggingTimer > 0) {
                    this.diggingTimer = 0;
                }
            }
        }
    }

    private void digAndDrop(World world, BlockPos pos, Random random) {
        world.setBlockState(pos, Blocks.DIRT.getDefaultState());

        if (!world.isClient()) {
            // 30% chance to drop an item
            if (random.nextFloat() <= 0.3f) {
                Optional<Item> droppedItem = getRandomItemFromTag(random, ModTags.Items.DROPS_FROM_BOAR_DIGGING);
                droppedItem.ifPresent(item -> {
                    ItemStack stack = new ItemStack(item);
                    world.spawnEntity(new net.minecraft.entity.ItemEntity(
                            world,
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            stack
                    ));
                });
            }
        }
    }

    private Optional<Item> getRandomItemFromTag(Random random, net.minecraft.registry.tag.TagKey<Item> tag) {
        java.util.List<Item> items = this.getWorld().getRegistryManager().get(net.minecraft.registry.RegistryKeys.ITEM).stream().filter(item -> item.getDefaultStack().isIn(tag)).toList();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.get(random.nextInt(items.size())));
    }

    //SOUNDS
    @Override
    public float getSoundPitch() {
        return 0.7f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) {
            return null;
        }
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }

    public static class BoarDigGoal extends Goal {
        private final BoarEntity boar;
        private int soundCooldown = 10;
        private BlockPos digPos;

        public BoarDigGoal(BoarEntity boar) {
            this.boar = boar;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.boar.diggingCooldown > 0) {
                return false;
            }
            if (this.boar.isBaby()) {
                return false;
            }
            if (this.boar.isSleeping()) {
                return false;
            }

            BlockPos pos = this.boar.getBlockPos().down();
            BlockState state = this.boar.getWorld().getBlockState(pos);

            return this.boar.getWorld().getBiome(this.boar.getBlockPos()).isIn(ModTags.Biomes.BOAR_CAN_DIG_IN) &&
                    (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.PODZOL));
        }

        @Override
        public void start() {
            this.boar.setDigging(true);
            this.boar.getNavigation().stop();
            this.digPos = this.boar.getBlockPos().down();
        }

        @Override
        public void stop() {
            this.boar.setDigging(false);
            this.boar.diggingCooldown = 6000;
        }

        @Override
        public boolean shouldContinue() {
            BlockPos pos = this.boar.getBlockPos().down();
            BlockState state = this.boar.getWorld().getBlockState(pos);

            if (!(this.boar.getWorld().getBiome(this.boar.getBlockPos()).isIn(ModTags.Biomes.BOAR_CAN_DIG_IN) &&
                    (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.PODZOL)))) {
                return false;
            }

            if (this.boar.isSleeping()) {
                return false;
            }

            //check if block changed
            if (!this.digPos.equals(this.boar.getBlockPos().down())) {
                return false;
            }

            return this.boar.isDigging();
        }

        @Override
        public void tick() {

            this.boar.getNavigation().stop();
            this.boar.getLookControl().tick();

            if (this.boar.isDigging()) {
                spawnDiggingParticles(this.boar);

                if (soundCooldown <= 0) {
                    this.boar.getWorld().playSound(null, this.boar.getX(), this.boar.getY(), this.boar.getZ(), SoundEvents.BLOCK_GRAVEL_STEP, this.boar.getSoundCategory(), 1.0F, 1.0F);
                    soundCooldown = 10;
                } else {
                    soundCooldown--;
                }
            }
        }

        private void spawnDiggingParticles(BoarEntity boar) {
            if (boar.getWorld() instanceof ServerWorld serverWorld) {
                double centerX = boar.getX();
                double centerY = boar.getY();
                double centerZ = boar.getZ();

                BlockStateParticleEffect dirtParticleEffect = new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.DIRT.getDefaultState());

                Vec3d headVec = boar.getRotationVec(0.0f);
                double particleX = centerX + headVec.x;
                double particleZ = centerZ + headVec.z;

                for (int i = 0; i < 5; i++) {
                    double offsetX = (boar.getRandom().nextDouble() - 0.5) * 0.5;
                    double offsetZ = (boar.getRandom().nextDouble() - 0.5) * 0.5;

                    serverWorld.spawnParticles(dirtParticleEffect,
                            particleX + offsetX,
                            centerY,
                            particleZ + offsetZ,
                            1, 0, 0, 0, 0.1);
                }
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.butterfly.ButterflyVariants;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.*;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.AboveGroundTargeting;
import net.minecraft.entity.ai.NoPenaltySolidTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
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
import java.util.Optional;

public class ButterflyEntity extends AnimalEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(ButterflyEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> HAS_POLLEN = DataTracker.registerData(ButterflyEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Nullable private BlockPos flowerPos;
    @Nullable private BlockPos cropPos;
    private int pollenTimer = 0;
    private int ticksUntilCanPollinate = 0;

    public ButterflyEntity(EntityType<? extends AnimalEntity> type, World world) {
        super(type, world);
        this.moveControl = new FlightMoveControl(this, 20, true);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.6);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(1, new TemptGoal(this, 1.0, (stack) -> stack.isIn(ModTags.Items.BUTTERFLY_FOR_BREEDING), false));
        this.goalSelector.add(2, new ButterflyPollinateGoal());
        this.goalSelector.add(2, new ButterflyFertilizeGoal());
        this.goalSelector.add(3, new ButterflyWanderAroundGoal(this));
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false; // butterfly should not be a baby
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(VARIANT, ButterflyVariants.YELLOW.getId());
        builder.add(HAS_POLLEN, false);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        if (spawnReason == SpawnReason.NATURAL || spawnReason == SpawnReason.COMMAND || spawnReason == SpawnReason.SPAWN_EGG || spawnReason == SpawnReason.CHUNK_GENERATION) {
            RegistryEntry<Biome> biomeEntry = world.getBiome(this.getBlockPos());
            Random random = world.getRandom();

            float roll = random.nextFloat();

            if (biomeEntry.isIn(ModTags.Biomes.SPAWNS_JUNGLE_BUTTERFLY)) {
                if (roll < 0.01f) {
                    this.setVariant(ButterflyVariants.BLACK);
                } else if (roll < 0.10f) {
                    this.setVariant(ButterflyVariants.GREEN);
                } else if (roll < 0.30f) {
                    this.setVariant(ButterflyVariants.BLUE);
                } else {
                    this.setVariant(ButterflyVariants.ORANGE);
                }
            } else if (biomeEntry.isIn(ModTags.Biomes.SPAWNS_PLAINS_BUTTERFLY)) {
                if (roll < 0.01f) {
                    this.setVariant(ButterflyVariants.BLACK);
                } else if (roll < 0.10f) {
                    this.setVariant(ButterflyVariants.ORANGE);
                } else if (roll < 0.30f) {
                    this.setVariant(ButterflyVariants.WHITE);
                } else if (roll < 0.50f) {
                    this.setVariant(ButterflyVariants.PURPLE);
                } else {
                    this.setVariant(ButterflyVariants.YELLOW);
                }
            } else if (biomeEntry.isIn(ModTags.Biomes.SPAWNS_FOREST_BUTTERFLY)) {
                if (roll < 0.01f) {
                    this.setVariant(ButterflyVariants.BLACK);
                } else if (roll < 0.10f) {
                    this.setVariant(ButterflyVariants.CRIMSON);
                } else if (roll < 0.30f) {
                    this.setVariant(ButterflyVariants.TERRACOTTA);
                } else {
                    this.setVariant(ButterflyVariants.BROWN);
                }
            } else {
                if (roll < 0.01f) {
                    this.setVariant(ButterflyVariants.BLACK);
                } else {
                    this.setVariant(ButterflyVariants.YELLOW);
                }
            }
        }

        return entityData;
    }

    protected EntityNavigation createNavigation(World world) {
        return new BirdNavigation(this, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.getVariant().getId());
        nbt.putBoolean("HasPollen", this.hasPollen());
        nbt.putInt("PollenTimer", this.pollenTimer);
        nbt.putInt("TicksUntilCanPollinate", this.ticksUntilCanPollinate);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setVariant(ButterflyVariants.byId(nbt.getInt("Variant")));
        this.setHasPollen(nbt.getBoolean("HasPollen"));
        this.pollenTimer = nbt.getInt("PollenTimer");
        this.ticksUntilCanPollinate = nbt.getInt("TicksUntilCanPollinate");
    }

    public boolean hasPollen() { return this.dataTracker.get(HAS_POLLEN); }

    public void setHasPollen(boolean hasPollen) {
        this.dataTracker.set(HAS_POLLEN, hasPollen);
        if (hasPollen) {
            this.pollenTimer = 4800; // 4 minutes
        } else {
            this.pollenTimer = 0;
        }
    }

    public ButterflyVariants getVariant() {
        return ButterflyVariants.byId(this.dataTracker.get(VARIANT));
    }

    public void setVariant(ButterflyVariants variant) {
        this.dataTracker.set(VARIANT, variant.getId());
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.hasPollen()) {
                if (--this.pollenTimer <= 0) {
                    this.setHasPollen(false);
                }
            }

            if (this.ticksUntilCanPollinate > 0) {
                this.ticksUntilCanPollinate--;
            }
        } else {
            if (this.hasPollen() && this.random.nextFloat() < 0.05F) {
                double xOffset = (this.random.nextDouble() - 0.5) * 0.2;
                double zOffset = (this.random.nextDouble() - 0.5) * 0.2;

                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.FALLING_NECTAR,
                        this.getX() + xOffset,
                        this.getY() + 0.1,
                        this.getZ() + zOffset,
                        0.0, 0.0, 0.0
                );
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        state.getController().setAnimation(RawAnimation.begin().then("animation.butterfly.fly", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld world, PassiveEntity mate) {
        CaterpillarEntity caterpillar = new CaterpillarEntity(ModEntities.CATERPILLAR, world);
        caterpillar.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());

        // call the caterpillar initialize method to get the variant behavior
        caterpillar.initialize(
                world,
                world.getLocalDifficulty(caterpillar.getBlockPos()),
                SpawnReason.BREEDING,
                null
        );

        return caterpillar;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isIn(ModTags.Items.BUTTERFLY_FOR_BREEDING);
    }

    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
    }

    public static class ButterflyWanderAroundGoal extends Goal {
        private final ButterflyEntity butterfly;

        public ButterflyWanderAroundGoal(ButterflyEntity butterfly) {
            this.butterfly = butterfly;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return butterfly.navigation.isIdle() && butterfly.random.nextInt(10) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return butterfly.navigation.isFollowingPath();
        }

        @Override
        public void start() {
            Vec3d vec3d = this.getRandomLocation();
            if (vec3d != null) {
                butterfly.navigation.startMovingAlong(
                        butterfly.navigation.findPathTo(BlockPos.ofFloored(vec3d), 1), 1.0
                );
            }
        }

        @Nullable
        private Vec3d getRandomLocation() {
            Vec3d vec3d2 = butterfly.getRotationVec(0.0F);

            Vec3d vec3d3 = AboveGroundTargeting.find(
                    butterfly, 8, 7, vec3d2.x, vec3d2.z, 1.5707964F, 3, 1
            );

            return vec3d3 != null
                    ? vec3d3
                    : NoPenaltySolidTargeting.find(
                    butterfly, 8, 4, -2, vec3d2.x, vec3d2.z, 1.5707963705062866
            );
        }
    }

    private class ButterflyPollinateGoal extends Goal {
        private int pollinationTicks = 0;

        public ButterflyPollinateGoal() {
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (ButterflyEntity.this.ticksUntilCanPollinate > 0 || ButterflyEntity.this.hasPollen() || ButterflyEntity.this.random.nextInt(20) != 0) {
                return false;
            }

            Optional<BlockPos> optional = findFlower();
            if (optional.isPresent()) {
                ButterflyEntity.this.flowerPos = optional.get();
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return ButterflyEntity.this.flowerPos != null && !ButterflyEntity.this.hasPollen() && ButterflyEntity.this.ticksUntilCanPollinate <= 0;
        }

        @Override
        public void start() {
            pollinationTicks = 0;
        }

        @Override
        public void tick() {
            BlockPos pos = ButterflyEntity.this.flowerPos;
            if (pos == null) return;

            ButterflyEntity.this.getLookControl().lookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            ButterflyEntity.this.getMoveControl().moveTo(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1.0);

            double dist = ButterflyEntity.this.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);

            if (dist < 1.5) {
                pollinationTicks++;
                if (pollinationTicks > 100) {
                    ButterflyEntity.this.setHasPollen(true);
                    this.stop();
                }
            }
        }

        private Optional<BlockPos> findFlower() {
            return BlockPos.findClosest(ButterflyEntity.this.getBlockPos(), 8, 4,
                    pos -> ButterflyEntity.this.getWorld().getBlockState(pos).isIn(BlockTags.FLOWERS));
        }

        @Override
        public void stop() {
            ButterflyEntity.this.flowerPos = null;
            pollinationTicks = 0;
        }
    }

    private class ButterflyFertilizeGoal extends Goal {
        private int fertilizeTicks = 0;

        public ButterflyFertilizeGoal() {
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (!ButterflyEntity.this.hasPollen() || ButterflyEntity.this.random.nextInt(10) != 0) return false;

            Optional<BlockPos> optional = findCrop();
            if (optional.isPresent()) {
                ButterflyEntity.this.cropPos = optional.get();
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            if (ButterflyEntity.this.cropPos == null || !ButterflyEntity.this.hasPollen()) return false;

            BlockState state = ButterflyEntity.this.getWorld().getBlockState(ButterflyEntity.this.cropPos);
            Block block = state.getBlock();
            if (block instanceof CropBlock crop) return !crop.isMature(state);
            if (block instanceof StemBlock) return state.get(StemBlock.AGE) < 7;
            if (state.isOf(Blocks.SWEET_BERRY_BUSH)) return state.get(SweetBerryBushBlock.AGE) < 3;

            return state.isIn(BlockTags.BEE_GROWABLES);
        }

        @Override
        public void start() {
            fertilizeTicks = 0;
        }

        @Override
        public void tick() {
            BlockPos pos = ButterflyEntity.this.cropPos;
            if (pos == null) return;

            ButterflyEntity.this.getLookControl().lookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            ButterflyEntity.this.getMoveControl().moveTo(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 1.0);

            double dist = ButterflyEntity.this.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5);

            if (dist < 1.5) {
                fertilizeTicks++;
                if (fertilizeTicks > 60) {
                    World world = ButterflyEntity.this.getWorld();
                    BlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    BlockState nextState = null;

                    if (state.isIn(BlockTags.BEE_GROWABLES)) {
                        if (block instanceof CropBlock cropBlock) {
                            if (!cropBlock.isMature(state)) {
                                nextState = cropBlock.withAge(cropBlock.getAge(state) + 1);
                            }
                        } else if (block instanceof StemBlock) {
                            int age = state.get(StemBlock.AGE);
                            if (age < 7) {
                                nextState = state.with(StemBlock.AGE, age + 1);
                            }
                        } else if (state.isOf(Blocks.SWEET_BERRY_BUSH)) {
                            int age = state.get(SweetBerryBushBlock.AGE);
                            if (age < 3) {
                                nextState = state.with(SweetBerryBushBlock.AGE, age + 1);
                            }
                        } else if (state.isOf(Blocks.CAVE_VINES) || state.isOf(Blocks.CAVE_VINES_PLANT)) {
                            ((Fertilizable)block).grow((ServerWorld)world, world.random, pos, state);
                        }

                        if (nextState != null) {
                            world.setBlockState(pos, nextState);
                            world.syncWorldEvent(2011, pos, 15);
                        }
                    }

                    ButterflyEntity.this.setHasPollen(false);
                    ButterflyEntity.this.ticksUntilCanPollinate = 6000;
                    this.stop();
                }
            }
        }

        private Optional<BlockPos> findCrop() {
            return BlockPos.findClosest(ButterflyEntity.this.getBlockPos(), 8, 4, pos -> {
                BlockState state = ButterflyEntity.this.getWorld().getBlockState(pos);
                if (!state.isIn(BlockTags.BEE_GROWABLES)) return false;

                Block block = state.getBlock();
                if (block instanceof CropBlock crop) return !crop.isMature(state);
                if (block instanceof StemBlock) return state.get(StemBlock.AGE) < 7;
                if (state.isOf(Blocks.SWEET_BERRY_BUSH)) return state.get(SweetBerryBushBlock.AGE) < 3;

                return true;
            });
        }

        @Override
        public void stop() {
            ButterflyEntity.this.cropPos = null;
            fertilizeTicks = 0;
        }
    }
}
package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.client.mud_golem.MudGolemVariants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EntityView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class MudGolemEntity extends TameableEntity implements GeoEntity {
    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(MudGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(MudGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Boolean> WET_STATE = DataTracker.registerData(MudGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    public static final TrackedData<Boolean> HAS_SNOW_LAYER = DataTracker.registerData(MudGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int snowTicks = 0;
    private int snowMeltTimer = 0;

    private static final int WET_TO_DRY_TIME = 400; // 20 seconds
    private static final int DRY_TO_WET_TIME = 400; // 20 seconds
    private int transitionTimer = 0;
    private boolean isWet = false;

    public MudGolemEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return TameableEntity .createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new SitGoal(this));
        this.goalSelector.add(1, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.75f, 1));
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(HAS_SNOW_LAYER, false);
        builder.add(SITTING, false);
        builder.add(VARIANT, MudGolemVariants.NORMAL.getId());
        builder.add(WET_STATE, false);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "land_controller", 5, this::landPredicate));
        controllers.add(new AnimationController<>(this, "water_controller", 5, this::waterPredicate));
    }

    private <T extends GeoAnimatable> PlayState landPredicate(AnimationState<T> state) {
        if (this.getVelocity().horizontalLengthSquared() > 1.0E-9) {
            state.getController().setAnimation(RawAnimation.begin().then("animation.mud_golem.walk", Animation.LoopType.LOOP));
        } else if (this.isSitting()){
            state.getController().setAnimation(RawAnimation.begin().then("animation.mud_golem.sit", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.mud_golem.idle", Animation.LoopType.LOOP));

        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState waterPredicate(AnimationState<T> state) {
        if (!this.isTouchingWater()) {
            return PlayState.STOP;
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("animation.mud_golem.swim", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
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

        if (isTouchingWaterOrRain()) {
            if (!isWet) {
                transitionTimer++;
                if (transitionTimer >= DRY_TO_WET_TIME) {
                    setWet(true);
                }
            } else {
                transitionTimer = 0; // Reset timer if already wet
            }
        } else {
            if (isWet) {
                transitionTimer++;
                if (transitionTimer >= WET_TO_DRY_TIME) {
                    setWet(false);
                }
            } else {
                transitionTimer = 0; // Reset timer if already dry
            }
        }
    }

    private void setWet(boolean wet) {
        this.isWet = wet;
        this.transitionTimer = 0;
        this.getDataTracker().set(WET_STATE, wet);

        if (wet) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.0);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
            this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(5.0);
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        if (itemstack.getItem() == Items.WHEAT_SEEDS && !isTamed()) {
            if (this.getWorld().isClient()) {
                return ActionResult.CONSUME;
            } else {
                if (!player.getAbilities().creativeMode) {
                    itemstack.decrement(1);
                }

                if (this.random.nextInt(3) == 0) {
                    super.setOwner(player);
                    this.navigation.recalculatePath();
                    this.setTarget(null);
                    this.getWorld().sendEntityStatus(this, (byte)7);
                }
                this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }

        if (isTamed() && !this.getWorld().isClient()) {
            if (itemstack.getItem() == Items.STICK && hand == Hand.MAIN_HAND) {
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
                    itemstack.damage(1, player, EquipmentSlot.MAINHAND);
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

    public boolean isSitting() {
        return this.dataTracker.get(SITTING);
    }

    public void setSit(PlayerEntity player, boolean sitting) {
        if (player == getOwner()) {
            this.dataTracker.set(SITTING, sitting);
            super.setSitting(sitting);
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("isSitting", this.dataTracker.get(SITTING));
        nbt.putInt("Variant", this.getVariant().ordinal());
        nbt.putBoolean("IsWet", this.isWet);
        if (this.getOwner() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putBoolean("HasSnowLayer", this.hasSnowLayer());
        nbt.putInt("SnowTicks", this.snowTicks);
        nbt.putInt("SnowMeltTimer", this.snowMeltTimer);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(SITTING, nbt.getBoolean("isSitting"));
        this.setVariant(MudGolemVariants.values()[nbt.getInt("Variant")]);
        setWet(nbt.getBoolean("IsWet"));
        UUID ownerUUID = nbt.contains("Owner") ? nbt.getUuid("Owner") : null;
        if (ownerUUID != null) {
            this.setOwnerUuid(ownerUUID);
        }
        this.setHasSnowLayer(nbt.getBoolean("HasSnowLayer"));
        this.snowTicks = nbt.getInt("SnowTicks");
        this.snowMeltTimer = nbt.getInt("SnowMeltTimer");
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
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

    public MudGolemVariants getVariant() {
        return MudGolemVariants.values()[this.dataTracker.get(VARIANT)];
    }

    public void setVariant(MudGolemVariants variant) {
        this.dataTracker.set(VARIANT, variant.ordinal());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geocache;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    //SOUNDS
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return this.getDataTracker().get(WET_STATE)
                ? SoundEvents.BLOCK_MUD_BREAK
                : SoundEvents.BLOCK_MUD_BRICKS_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        SoundEvent stepSound = this.getDataTracker().get(WET_STATE)
                ? SoundEvents.BLOCK_MUD_STEP
                : SoundEvents.BLOCK_MUD_BRICKS_STEP;

        this.playSound(stepSound, 0.15F, 1.0F);
    }
}
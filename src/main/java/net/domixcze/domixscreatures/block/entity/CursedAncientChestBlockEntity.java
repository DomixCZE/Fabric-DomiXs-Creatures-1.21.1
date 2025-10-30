package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.block.custom.CursedAncientChestBlock;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// /setblock ~ ~ ~ domixs-creatures:cursed_ancient_chest_block[facing=north]{LootTable:"domixs-creatures:chests/aztec_temple"} useful command :)
public class CursedAncientChestBlockEntity extends LootableContainerBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int OPEN_CLOSE_EVENT = 1;

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    public final List<UUID> spawnedMobUUIDs = new ArrayList<>();

    private boolean mobsSpawned = false;
    private boolean isChestOpen = false;

    private final ViewerCountManager stateManager = new ViewerCountManager() {
        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            world.playSound(null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.ANCIENT_CHEST_OPEN, SoundCategory.BLOCKS,
                    0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            world.addSyncedBlockEvent(pos, state.getBlock(), OPEN_CLOSE_EVENT, 1);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            world.playSound(null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.ANCIENT_CHEST_CLOSE, SoundCategory.BLOCKS,
                    0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            world.addSyncedBlockEvent(pos, state.getBlock(), OPEN_CLOSE_EVENT, 0);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldCount, int newCount) {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler handler) {
                return handler.getInventory() == CursedAncientChestBlockEntity.this;
            }
            return false;
        }
    };

    public CursedAncientChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CURSED_ANCIENT_CHEST_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected Text getContainerName() {
        return Text.literal("Ancient Chest");
    }

    @Override
    public DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    private void spawnMobGroup(ServerWorld world, BlockPos pos, Random random, int playerCount) {
        int roll = random.nextInt(4);

        int baseCount = switch (roll) {
            case 0 -> 5; // zombies
            case 1 -> 4; // ancient skeletons.json
            case 2 -> 5; // spiders
            case 3 -> 3; // cave spiders
            default -> 0;
        };

        EntityType<?> type = switch (roll) {
            case 1 -> ModEntities.ANCIENT_SKELETON;
            case 2 -> EntityType.SPIDER;
            case 3 -> EntityType.CAVE_SPIDER;
            default -> EntityType.ZOMBIE;
        };

        // Extra mobs based on player count (max 5)
        int extra = Math.max(0, playerCount - 1);
        extra = Math.min(extra, 5);
        int totalCount = baseCount + extra;

        Direction facing = getCachedState().get(CursedAncientChestBlock.FACING);

        for (int i = 0; i < totalCount; i++) {
            for (int attempt = 0; attempt < 5; attempt++) {
                BlockPos front = pos.offset(facing, 2);
                BlockPos spread = front.add(
                        random.nextBetween(-1, 1),
                        0,
                        random.nextBetween(-1, 1)
                );

                if (world.getBlockState(spread).isAir() && world.getBlockState(spread.up()).isAir()) {
                    var entity = type.create(world);
                    if (entity != null) {
                        entity.refreshPositionAndAngles(
                                spread.getX() + 0.5,
                                spread.getY(),
                                spread.getZ() + 0.5,
                                world.random.nextFloat() * 360F,
                                0.0F
                        );

                        if (entity instanceof MobEntity mobEntity) {
                            mobEntity.initialize(world, world.getLocalDifficulty(spread), SpawnReason.MOB_SUMMONED, null);
                        }
                        world.spawnEntity(entity);
                        spawnedMobUUIDs.add(entity.getUuid());
                        break;
                    }
                }
            }
        }
    }

    public void unlock() {
        if (world != null) {
            world.setBlockState(pos, getCachedState().with(CursedAncientChestBlock.CURSED, false));
            markDirty();
        }
    }

    public void tick() {
        if (!spawnedMobUUIDs.isEmpty() && world instanceof ServerWorld serverWorld) {
            spawnedMobUUIDs.removeIf(uuid -> serverWorld.getEntity(uuid) == null);

            if (spawnedMobUUIDs.isEmpty()) {
                unlock();
            }
        }

        if (world != null && !world.isClient) {
            stateManager.updateViewerCount(world, pos, getCachedState());
        }
    }

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.ancient_chest.open");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("animation.ancient_chest.close");

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, this, 3);
    }

    public ActionResult onUse(PlayerEntity player, BlockState state) {
        if (state.get(CursedAncientChestBlock.CURSED)) {
            if (!mobsSpawned && world instanceof ServerWorld serverWorld) {
                world.playSound(null,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.BLOCKS,
                        0.5F, world.random.nextFloat() * 0.1F + 0.9F);

                mobsSpawned = true;

                List<PlayerEntity> players = world.getEntitiesByClass(
                        PlayerEntity.class,
                        new Box(pos).expand(16),
                        p -> true
                );

                int playerCount = players.size();
                spawnMobGroup(serverWorld, pos, serverWorld.getRandom(), playerCount);
            }
            return ActionResult.CONSUME;
        }

        if (world != null && !world.isClient) {
            this.generateLoot(player);
            player.openHandledScreen(this);
            stateManager.openContainer(player, world, pos, getCachedState());
            return ActionResult.CONSUME;
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == OPEN_CLOSE_EVENT) {
            isChestOpen = data == 1;
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 1, state -> {
            if (isChestOpen) {
                if (!state.isCurrentAnimation(OPEN_ANIM)) {
                    return state.setAndContinue(OPEN_ANIM);
                } else {
                    return PlayState.CONTINUE;
                }
            } else {
                if (!state.isCurrentAnimation(CLOSE_ANIM) && state.getController().getCurrentAnimation() != null) {
                    return state.setAndContinue(CLOSE_ANIM);
                } else if (state.isCurrentAnimation(CLOSE_ANIM)) {
                    return PlayState.CONTINUE;
                } else {
                    return PlayState.STOP;
                }
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }
        mobsSpawned = nbt.getBoolean("MobsSpawned");
        if (world != null && nbt.contains("Cursed")) {
            boolean cursed = nbt.getBoolean("Cursed");
            world.setBlockState(pos, getCachedState().with(CursedAncientChestBlock.CURSED, cursed), 3);
        }
        spawnedMobUUIDs.clear();
        if (nbt.contains("SpawnedMobUUIDs")) {
            NbtCompound uuidList = nbt.getCompound("SpawnedMobUUIDs");
            for (String key : uuidList.getKeys()) {
                spawnedMobUUIDs.add(uuidList.getUuid(key));
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
        nbt.putBoolean("Cursed", getCachedState().get(CursedAncientChestBlock.CURSED));
        nbt.putBoolean("MobsSpawned", mobsSpawned);
        NbtCompound uuidList = new NbtCompound();
        for (int i = 0; i < spawnedMobUUIDs.size(); i++) {
            uuidList.putUuid("uuid_" + i, spawnedMobUUIDs.get(i));
        }
        nbt.put("SpawnedMobUUIDs", uuidList);
    }
}
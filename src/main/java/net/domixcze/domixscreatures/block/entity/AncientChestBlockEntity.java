package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AncientChestBlockEntity extends LootableContainerBlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final int OPEN_CLOSE_EVENT = 1;

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);

    private boolean isChestOpen = false;

    public AncientChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANCIENT_CHEST_BLOCK_ENTITY, pos, state);
    }

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
                return handler.getInventory() == AncientChestBlockEntity.this;
            }
            return false;
        }
    };

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

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("animation.ancient_chest.open");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlay("animation.ancient_chest.close");

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, this, 3);
    }

    public ActionResult onUse(PlayerEntity player, BlockState state) {
        if (world != null && !world.isClient) {
            this.generateLoot(player);
            player.openHandledScreen(this);
            this.stateManager.openContainer(player, world, pos, getCachedState());
            return ActionResult.CONSUME;
        }
        return ActionResult.SUCCESS;
    }

    public void tick() {
        if (world != null && !world.isClient) {
            stateManager.updateViewerCount(world, pos, getCachedState());
        }
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }
}
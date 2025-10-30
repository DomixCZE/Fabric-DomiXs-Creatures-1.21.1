package net.domixcze.domixscreatures.screen.custom;

import net.domixcze.domixscreatures.screen.ModScreenHandlers;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class FishTrapScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public FishTrapScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos));
    }

    public FishTrapScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.FISH_TRAP_SCREEN_HANDLER, syncId);
        this.inventory = (Inventory) blockEntity;

        int slotSize = 18;
        int gridSize = 3;

        int lootStartX = (176 - slotSize * gridSize) / 2 + 1;
        int lootStartY = 17;

        int lootStartIndex = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int slotIndex = lootStartIndex + row * gridSize + col;
                this.addSlot(new Slot(inventory, slotIndex,
                        lootStartX + col * slotSize,
                        lootStartY + row * slotSize) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false; // output-only
                    }
                });
            }
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack original = slot.getStack();
            newStack = original.copy();

            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(original, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(original, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9,
                        8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
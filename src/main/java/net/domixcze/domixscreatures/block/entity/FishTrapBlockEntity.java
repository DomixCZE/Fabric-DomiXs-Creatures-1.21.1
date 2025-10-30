package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.screen.custom.FishTrapScreenHandler;
import net.domixcze.domixscreatures.util.ModTags;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FishTrapBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, SidedInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    private int fishingCooldown = 0;
    private final int ticksPerItem = 4800; // 5 items per day

    public FishTrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISH_TRAP_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, FishTrapBlockEntity entity) {
        if (world.isClient) return;

        entity.fishingCooldown++;
        if (entity.fishingCooldown >= entity.ticksPerItem) {
            entity.fishingCooldown = 0;
            if (entity.canFish()) {
                entity.catchItem();
            }
        }
    }

    public boolean canFish() {
        if (this.world == null) return false;
        if (!isWaterlogged()) return false;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos checkPos = pos.add(dx, 0, dz);
                BlockState state = world.getBlockState(checkPos);
                if (!state.isOf(Blocks.WATER)) {
                    return false;
                }
            }
        }

        for (ItemStack stack : items) {
            if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) {
                return true;
            }
        }

        return false;
    }

    private boolean isWaterlogged() {
        if (this.world == null) return false;
        return world.getBlockState(pos).get(Properties.WATERLOGGED);
    }

    private void catchItem() {
        if (!(this.world instanceof ServerWorld serverWorld)) return;

        RegistryKey<LootTable> lootTableKey = LootTables.FISHING_GAMEPLAY;

        RegistryEntry<Biome> biomeEntry = serverWorld.getRegistryManager()
                .get(RegistryKeys.BIOME)
                .getEntry(serverWorld.getBiome(pos).getKey().orElse(null))
                .orElse(null);

        if (biomeEntry != null && serverWorld.getRandom().nextFloat() < 0.35f) {
            if (biomeEntry.isIn(ModTags.Biomes.FISH_TRAP_OCEAN)) {
                lootTableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE,
                        Identifier.of(DomiXsCreatures.MOD_ID, "gameplay/fish_trap_ocean"));
            } else if (biomeEntry.isIn(ModTags.Biomes.FISH_TRAP_JUNGLE)) {
                lootTableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE,
                        Identifier.of(DomiXsCreatures.MOD_ID, "gameplay/fish_trap_jungle"));
            } else if (biomeEntry.isIn(ModTags.Biomes.FISH_TRAP_SWAMP)) {
                lootTableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE,
                        Identifier.of(DomiXsCreatures.MOD_ID, "gameplay/fish_trap_swamp"));
            } else if (biomeEntry.isIn(ModTags.Biomes.FISH_TRAP_LUSH_CAVE)) {
                lootTableKey = RegistryKey.of(RegistryKeys.LOOT_TABLE,
                        Identifier.of(DomiXsCreatures.MOD_ID, "gameplay/fish_trap_lush_cave"));
            }
        }

        LootTable lootTable = serverWorld.getServer()
                .getReloadableRegistries()
                .getLootTable(lootTableKey);

        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, ItemStack.EMPTY);
        LootContextParameterSet context = builder.build(LootContextTypes.FISHING);

        List<ItemStack> loot = lootTable.generateLoot(context);

        for (ItemStack stack : loot) {
            if (tryInsertStack(stack)) {
                break;
            }
        }
    }

    private boolean tryInsertStack(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (ItemStack existing : items) {
            if (!existing.isEmpty() && ItemStack.areItemsEqual(existing, stack) && existing.getCount() < existing.getMaxCount()) {
                int transfer = Math.min(existing.getMaxCount() - existing.getCount(), stack.getCount());
                existing.increment(transfer);
                stack.decrement(transfer);
                markDirty();
                return true;
            }
        }

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isEmpty()) {
                items.set(i, stack.copy());
                stack.setCount(0);
                markDirty();
                return true;
            }
        }

        return false;
    }

    public void dropInventory(World world, BlockPos pos) {
        if (world.isClient) return;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) Block.dropStack(world, pos, stack);
        }
        clear();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, items, registryLookup);
        nbt.putInt("fishingCooldown", fishingCooldown);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, items, registryLookup);
        fishingCooldown = nbt.getInt("fishingCooldown");
    }

    @Override
    public int size() { return items.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) if (!stack.isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) { return items.get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(items, slot, amount);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) { return Inventories.removeStack(items, slot); }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
        markDirty();
    }

    @Override
    public void clear() { items.clear(); }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return true; }

    @Override
    public void markDirty() { super.markDirty(); }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0,1,2,3,4,5,6,7,8};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        return false; // no manual inserts
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return true;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) { return this.pos; }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block_entity.domixs_creatures.fish_trap");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FishTrapScreenHandler(syncId, playerInventory, this.pos);
    }
}
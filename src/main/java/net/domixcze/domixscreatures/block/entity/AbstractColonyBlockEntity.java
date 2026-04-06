package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.entity.ai.goal.util.ColonyMember;
import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public abstract class AbstractColonyBlockEntity extends BlockEntity {
    private static final List<String> IRRELEVANT_NBT_KEYS = Arrays.asList(
            "Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot",
            "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances",
            "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion",
            "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "UUID", "Passengers", "leash", "Health"
    );

    protected final List<NbtCompound> storedMembers = new ArrayList<>();
    protected final Set<UUID> activeMembers = new HashSet<>();
    protected boolean hasQueen = false;
    protected int spawnTimer = 0;

    public AbstractColonyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected abstract EntityType<? extends MobEntity> getTargetEntityType();
    protected abstract Block getMoundBlock();
    protected abstract void onMemberReleased(MobEntity entity);

    public boolean hasQueen() {
        return this.hasQueen;
    }

    public void setHasQueen(boolean hasQueen) {
        this.hasQueen = hasQueen;
        this.markDirty();
    }

    public Block getColonyMound() {
        return this.getMoundBlock();
    }

    public int getTotalMembers() {
        return storedMembers.size() + activeMembers.size();
    }

    public void tryEnter(MobEntity entity) {
        if (entity.getType() != getTargetEntityType()) return;
        if (getTotalMembers() > 10) return;

        NbtCompound nbt = new NbtCompound();
        entity.saveNbt(nbt);
        IRRELEVANT_NBT_KEYS.forEach(nbt::remove);

        storedMembers.add(nbt);
        activeMembers.remove(entity.getUuid());

        entity.getWorld().playSound(
                null, pos,
                SoundEvents.BLOCK_GRAVEL_BREAK,
                SoundCategory.BLOCKS,
                0.5f, 1.5f
        );

        entity.discard();
        markDirty();
    }

    public void unregisterMember(UUID uuid) {
        if (activeMembers.remove(uuid)) {
            markDirty();
        }
    }

    public boolean registerMember(UUID uuid) {
        if (getTotalMembers() < 10) {
            if (activeMembers.add(uuid)) {
                markDirty();
            }
            return true;
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractColonyBlockEntity be) {
        if (world.isClient) return;

        if (be.hasQueen && be.getTotalMembers() < 10) {
            be.spawnTimer++;

            if (be.spawnTimer >= 1200) {
                NbtCompound nbt = new NbtCompound();
                nbt.putString("id", EntityType.getId(be.getTargetEntityType()).toString());
                be.storedMembers.add(nbt);
                be.spawnTimer = 0;
                be.markDirty();
            }
        }

        if (!be.storedMembers.isEmpty() && world.random.nextInt(200) == 0) {
            be.releaseMember((ServerWorld) world);
        }
    }

    public void releaseAll() {
        if (!(world instanceof ServerWorld serverWorld)) return;

        while (!storedMembers.isEmpty()) {
            releaseMember(serverWorld);
        }
    }

    private void releaseMember(ServerWorld world) {
        if (storedMembers.isEmpty()) return;

        NbtCompound nbt = storedMembers.removeFirst();
        boolean isFreshSpawn = nbt.getSize() <= 1;

        MobEntity entity = (MobEntity) EntityType.loadEntityWithPassengers(nbt, world, e -> e);
        if (entity == null) return;

        if (entity instanceof TermiteEntity termite) {
            termite.setHasSawdust(false);
        }

        if (entity instanceof AntEntity ant) {
            ant.setHasLeaves(false);
        }

        BlockPos exit = findExitPos(world, pos);
        entity.refreshPositionAndAngles(
                exit.getX() + 0.5,
                exit.getY(),
                exit.getZ() + 0.5,
                world.random.nextFloat() * 360f,
                0
        );

        if (entity instanceof ColonyMember member) {
            member.setHomePos(pos);
        }

        if (isFreshSpawn) {
            onMemberReleased(entity);
        }

        world.spawnEntity(entity);
        activeMembers.add(entity.getUuid());
        markDirty();
    }

    private BlockPos findExitPos(World world, BlockPos pos) {
        for (BlockPos p : BlockPos.iterateOutwards(pos, 3, 2, 3)) {
            if (world.getBlockState(p).isOf(getMoundBlock())) {
                for (Direction d : Direction.values()) {
                    if (world.isAir(p.offset(d))) {
                        return p.offset(d);
                    }
                }
            }
        }
        return pos.up();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("HasQueen", hasQueen);
        nbt.putInt("SpawnTimer", spawnTimer);
        NbtList list = new NbtList();
        list.addAll(storedMembers);
        nbt.put("StoredMembers", list);

        NbtList active = new NbtList();
        for (UUID uuid : activeMembers) {
            active.add(NbtHelper.fromUuid(uuid));
        }
        nbt.put("ActiveMembers", active);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.hasQueen = nbt.getBoolean("HasQueen");
        this.spawnTimer = nbt.getInt("SpawnTimer");
        this.storedMembers.clear();
        NbtList list = nbt.getList("StoredMembers", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            storedMembers.add(list.getCompound(i));
        }
        this.activeMembers.clear();
        NbtList active = nbt.getList("ActiveMembers", NbtElement.INT_ARRAY_TYPE);
        for (NbtElement nbtElement : active) {
            activeMembers.add(NbtHelper.toUuid(nbtElement));
        }
    }
}

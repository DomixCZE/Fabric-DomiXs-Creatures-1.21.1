package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.block.custom.MolehillBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class MolehillBlockEntity extends BlockEntity {
    private NbtCompound storedMoleData;

    public MolehillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOLEHILL_BLOCK_ENTITY, pos, state);
    }

    public boolean isOccupied() {
        return storedMoleData != null || this.getCachedState().get(MolehillBlock.OCCUPIED);
    }

    public void storeMoleData(NbtCompound moleData) {
        this.storedMoleData = moleData;
        markDirty();
    }

    public NbtCompound releaseMoleData() {
        NbtCompound data = this.storedMoleData;
        if(data != null){
            data.putBoolean("InsideHill", false);
            this.storeMoleData(data);
        }
        this.storedMoleData = null;
        markDirty();
        return data;
    }

    public static Optional<MolehillBlockEntity> findNearbyUnoccupiedHill(ServerWorld world, BlockPos pos) {
        List<BlockPos> possibleHills = BlockPos.stream(pos.add(-5, -2, -5), pos.add(5, 2, 5))
                .map(BlockPos::toImmutable)
                .filter(blockPos -> world.getBlockEntity(blockPos) instanceof MolehillBlockEntity)
                .toList();

        for (BlockPos hillPos : possibleHills) {
            MolehillBlockEntity hillEntity = (MolehillBlockEntity) world.getBlockEntity(hillPos);
            if (hillEntity != null && !hillEntity.isOccupied()) {
                return Optional.of(hillEntity);
            }
        }
        return Optional.empty();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("MoleData")) {
            this.storedMoleData = nbt.getCompound("MoleData");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.storedMoleData != null) {
            nbt.put("MoleData", this.storedMoleData);
        }
    }
}
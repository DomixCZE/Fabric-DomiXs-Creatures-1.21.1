package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractColonyBlock extends BlockWithEntity {
    protected AbstractColonyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, getBlockEntityType(), AbstractColonyBlockEntity::tick);
    }

    protected abstract BlockEntityType<? extends AbstractColonyBlockEntity> getBlockEntityType();

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractColonyBlockEntity colony) {
                if (!world.isClient) {
                    colony.releaseAll();

                    if (colony.hasQueen()) {
                        spawnQueen(world, pos, colony);
                    }
                }
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    protected abstract void spawnQueen(World world, BlockPos pos, AbstractColonyBlockEntity colony);
}
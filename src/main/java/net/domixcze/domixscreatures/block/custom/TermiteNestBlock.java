package net.domixcze.domixscreatures.block.custom;

import com.mojang.serialization.MapCodec;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.block.entity.ModBlockEntities;
import net.domixcze.domixscreatures.block.entity.TermiteNestBlockEntity;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.termite.TermiteVariants;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TermiteNestBlock extends AbstractColonyBlock {
    public static final MapCodec<TermiteNestBlock> CODEC = TermiteNestBlock.createCodec(TermiteNestBlock::new);

    public TermiteNestBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<? extends AbstractColonyBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TERMITE_NEST_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TermiteNestBlockEntity(pos, state);
    }

    @Override
    protected void spawnQueen(World world, BlockPos pos, AbstractColonyBlockEntity colony) {
        TermiteEntity queen = (TermiteEntity) ModEntities.TERMITE.create(world);
        if (queen != null) {
            queen.setVariant(TermiteVariants.QUEEN);
            queen.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.random.nextFloat() * 360, 0);
            world.spawnEntity(queen);
            colony.setHasQueen(false);
        }
    }
}
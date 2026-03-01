package net.domixcze.domixscreatures.block.custom;

import com.mojang.serialization.MapCodec;
import net.domixcze.domixscreatures.block.entity.AbstractColonyBlockEntity;
import net.domixcze.domixscreatures.block.entity.AntNestBlockEntity;
import net.domixcze.domixscreatures.block.entity.ModBlockEntities;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.ant.AntVariants;
import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AntNestBlock extends AbstractColonyBlock {
    public static final MapCodec<TermiteNestBlock> CODEC = TermiteNestBlock.createCodec(TermiteNestBlock::new);

    public AntNestBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockEntityType<? extends AbstractColonyBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ANT_NEST_BLOCK_ENTITY;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AntNestBlockEntity(pos, state);
    }

    @Override
    protected void spawnQueen(World world, BlockPos pos, AbstractColonyBlockEntity colony) {
        AntEntity queen = (AntEntity) ModEntities.ANT.create(world);
        if (queen != null) {
            queen.setVariant(AntVariants.QUEEN);
            queen.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, world.random.nextFloat() * 360, 0);
            world.spawnEntity(queen);
            colony.setHasQueen(false);
        }
    }
}
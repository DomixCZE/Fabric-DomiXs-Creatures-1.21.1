package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.termite.TermiteVariants;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class TermiteNestBlockEntity extends AbstractColonyBlockEntity {
    public TermiteNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TERMITE_NEST_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected EntityType<? extends MobEntity> getTargetEntityType() {
        return ModEntities.TERMITE;
    }

    @Override
    protected Block getMoundBlock() {
        return ModBlocks.TERMITE_MOUND;
    }

    @Override
    protected void onMemberReleased(MobEntity entity) {
        if (entity instanceof TermiteEntity termite) {
            float roll = entity.getWorld().getRandom().nextFloat();

            if (roll < 0.3f) {
                termite.setVariant(TermiteVariants.SOLDIER);
            } else {
                termite.setVariant(TermiteVariants.WORKER);
            }
        }
    }
}
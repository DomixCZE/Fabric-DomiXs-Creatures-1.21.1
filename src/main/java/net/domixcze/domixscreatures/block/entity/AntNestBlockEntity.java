package net.domixcze.domixscreatures.block.entity;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.client.ant.AntVariants;
import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

public class AntNestBlockEntity extends AbstractColonyBlockEntity {
    public AntNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANT_NEST_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected EntityType<? extends MobEntity> getTargetEntityType() {
        return ModEntities.ANT;
    }

    @Override
    protected Block getMoundBlock() {
        return ModBlocks.ANT_MOUND;
    }

    @Override
    protected void onMemberReleased(MobEntity entity) {
        if (entity instanceof AntEntity ant) {
            float roll = entity.getWorld().getRandom().nextFloat();

            if (roll < 0.3f) {
                ant.setVariant(AntVariants.SOLDIER);
            } else {
                ant.setVariant(AntVariants.WORKER);
            }
        }
    }
}
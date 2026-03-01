package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CrackedGlassBlock extends TransparentBlock {
    public CrackedGlassBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient) {
            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.breakBlock(pos, false);
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.getGameRules().getBoolean(ModGameRules.ENABLE_RAIN_BREAKING_CRACKED_GLASS)) {
            return;
        }

        if (world.isRaining() && world.isSkyVisible(pos)) {
            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.breakBlock(pos, false);
        }
    }
}

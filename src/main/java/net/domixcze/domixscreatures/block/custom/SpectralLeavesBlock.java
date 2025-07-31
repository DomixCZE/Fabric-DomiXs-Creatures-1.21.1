package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.particle.ModParticles;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SpectralLeavesBlock extends LeavesBlock {

    public SpectralLeavesBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        if (random.nextInt(10) == 0) {
            BlockPos below = pos.down();
            BlockState belowState = world.getBlockState(below);
            if (!isFaceFullSquare(belowState.getCollisionShape(world, below), Direction.UP)) {
                ParticleUtil.spawnParticle(world, pos, random, ModParticles.SPECTRAL_LEAVES);
            }
        }
    }
}
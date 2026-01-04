package net.domixcze.domixscreatures.block.custom;

import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpikeTrapBlock extends Block {
    public static final BooleanProperty EXTENDED = BooleanProperty.of("extended");

    public SpikeTrapBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(EXTENDED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(EXTENDED);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block,
                               BlockPos fromPos, boolean notify) {
        if (world.isClient) return;

        boolean powered = world.isReceivingRedstonePower(pos);
        boolean extended = state.get(EXTENDED);

        if (powered && !extended) {
            world.setBlockState(pos, state.with(EXTENDED, true), Block.NOTIFY_LISTENERS);
            world.playSound(null, pos, ModSounds.SPIKE_TRAP_EXTEND, SoundCategory.BLOCKS, 1.0F, 1.0F);
        } else if (!powered && extended) {
            world.setBlockState(pos, state.with(EXTENDED, false), Block.NOTIFY_LISTENERS);
            world.playSound(null, pos, ModSounds.SPIKE_TRAP_RETRACT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (state.get(EXTENDED) && entity instanceof LivingEntity living) {
            Vec3d slow = new Vec3d(0.25, 0.05, 0.25);
            living.slowMovement(state, slow);

            if (!world.isClient) {
                living.damage(world.getDamageSources().cactus(), 10.0F);
            }
        }

        super.onSteppedOn(world, pos, state, entity);
    }
}
package net.domixcze.domixscreatures.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetBlock extends Block {

    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;

    protected static final VoxelShape SHAPE_BOTTOM = Block.createCuboidShape(0, 3, 0, 16, 5, 16);
    protected static final VoxelShape SHAPE_TOP = Block.createCuboidShape(0, 11, 0, 16, 13, 16);

    public NetBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(HALF, BlockHalf.BOTTOM));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(HALF) == BlockHalf.TOP ? SHAPE_TOP : SHAPE_BOTTOM;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        boolean top = ctx.getHitPos().y - ctx.getBlockPos().getY() > 0.5;
        return this.getDefaultState().with(HALF, top ? BlockHalf.TOP : BlockHalf.BOTTOM);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.handleFallDamage(fallDistance, 0.0F, world.getDamageSources().fall());
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        this.bounce(entity);
    }

    private void bounce(Entity entity) {
        Vec3d vec3d = entity.getVelocity();
        if (vec3d.y < 0.0) {
            double d = entity instanceof LivingEntity ? 0.4 : 0.25;
            entity.setVelocity(vec3d.x, -vec3d.y * d, vec3d.z);
        }
    }
}
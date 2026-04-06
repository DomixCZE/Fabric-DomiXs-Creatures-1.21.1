package net.domixcze.domixscreatures.entity.custom;

import net.domixcze.domixscreatures.entity.ai.goal.util.Chainable;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChainAnchorEntity extends AbstractDecorationEntity {

    public ChainAnchorEntity(EntityType<? extends AbstractDecorationEntity> type, World world) {
        super(type, world);
    }

    public ChainAnchorEntity(EntityType<? extends AbstractDecorationEntity> type, World world, BlockPos pos) {
        super(type, world, pos);
        this.setPosition(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(MathHelper.floor(x) + 0.5D, MathHelper.floor(y) + 0.5D, MathHelper.floor(z) + 0.5D);
    }

    @Override
    protected Box calculateBoundingBox(BlockPos pos, Direction side) {
        double xSize = 0.3125D;
        double ySize = 0.25D;
        double yOffset = 0.625D;

        return new Box(
                pos.getX() + 0.5 - xSize, pos.getY() + yOffset, pos.getZ() + 0.5 - xSize,
                pos.getX() + 0.5 + xSize, pos.getY() + yOffset + ySize, pos.getZ() + 0.5 + xSize
        );
    }

    @Override
    public void onBreak(@Nullable Entity breaker) {
        this.playSound(SoundEvents.BLOCK_CHAIN_BREAK, 1.0F, 1.0F);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.BLOCK_CHAIN_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        super.remove(removalReason);
        if (!this.getWorld().isClient) {
            List<Entity> attachedEntities = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(30.0D),
                    entity -> entity instanceof Chainable c && this.equals(c.getChainHolder()));

            for (Entity entity : attachedEntities) {
                ((Chainable) entity).detachChain(false);

                this.dropItem(ModItems.IRON_SHACKLE);
            }
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) return ActionResult.SUCCESS;

        List<Entity> leashedByPlayer = this.getWorld().getOtherEntities(player, player.getBoundingBox().expand(30.0D),
                entity -> entity instanceof Chainable c && player.equals(c.getChainHolder()));

        if (!leashedByPlayer.isEmpty()) {
            for (Entity entity : leashedByPlayer) {
                ((Chainable) entity).attachChain(this);
            }
            return ActionResult.SUCCESS;
        }

        List<Entity> attachedToAnchor = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(30.0D),
                entity -> entity instanceof Chainable c && this.equals(c.getChainHolder()));

        if (!attachedToAnchor.isEmpty()) {
            for (Entity entity : attachedToAnchor) {
                ((Chainable) entity).detachChain(false);
                if (!player.getAbilities().creativeMode) {
                    this.dropItem(ModItems.IRON_SHACKLE);
                }
            }
            this.discard();
            return ActionResult.SUCCESS;
        }

        this.discard();
        if (!player.getAbilities().creativeMode) {
            this.dropItem(ModItems.IRON_SHACKLE);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean canStayAttached() {
        return super.canStayAttached() || this.getWorld().getBlockState(this.attachedBlockPos).isIn(BlockTags.WALLS);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("TileX", this.attachedBlockPos.getX());
        nbt.putInt("TileY", this.attachedBlockPos.getY());
        nbt.putInt("TileZ", this.attachedBlockPos.getZ());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.attachedBlockPos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));
    }
}
package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.entity.custom.QuillProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class QuillItem extends Item implements ProjectileItem {
    public QuillItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        return new QuillProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
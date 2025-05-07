package net.domixcze.domixscreatures.util;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class QuillDispenserBehavior extends ProjectileDispenserBehavior {

    public QuillDispenserBehavior(Item item) {
        super(item);
    }

    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.world();
        Direction direction = pointer.state().get(DispenserBlock.FACING);
        Position position = DispenserBlock.getOutputLocation(pointer);
        Item dispensedItem = stack.getItem();
        if (dispensedItem instanceof ProjectileItem projectileItem) {
            ProjectileEntity projectileEntity = projectileItem.createEntity(world, position, stack, direction);
            projectileEntity.setVelocity((double)direction.getOffsetX(), (double)((float)direction.getOffsetY() + 0.1F), (double)direction.getOffsetZ(), 0.75F, 6.0F); // Use your original force and variation
            world.spawnEntity(projectileEntity);
            stack.decrement(1);
            return stack;
        }
        return super.dispenseSilently(pointer, stack);
    }
}
package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class MagnetItem extends Item {
    public MagnetItem(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    private MovingSoundInstance activeMagnetSound;

    @Override
    public boolean onClicked(ItemStack magnetStack, ItemStack cursorStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorRef) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) return false;

        var bound = magnetStack.get(ModDataComponents.BOUND_ITEM);

        // Remove: right-click magnet while holding nothing
        if (cursorStack.isEmpty() && bound != null) {
            ItemStack result = bound.stack().copy();
            magnetStack.remove(ModDataComponents.BOUND_ITEM);
            player.playSound(ModSounds.MAGNET_OUT, 1.0F, 1.0F);
            cursorRef.set(result);
            return true;
        }

        if (bound == null && !cursorStack.isEmpty()) {
            if (cursorStack.isOf(this)) {
                return true;
            }

            // Insert: right-click magnet with item to bind
            ItemStack toBind = cursorStack.copyWithCount(1);
            magnetStack.set(ModDataComponents.BOUND_ITEM, new ModDataComponents.BoundItemComponent(toBind));
            cursorStack.decrement(1);
            player.playSound(ModSounds.MAGNET_IN, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(user instanceof PlayerEntity player)) return;

        var boundComponent = stack.get(ModDataComponents.BOUND_ITEM);
        if (boundComponent == null) return;

        ItemStack bound = boundComponent.stack();
        if (bound.isEmpty()) return;

        boolean pushing = player.isSneaking();
        Vec3d origin = player.getPos();
        double range = 5.0;

        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, player.getBoundingBox().expand(range), item ->
                item.getStack().isOf(bound.getItem()));

        double forceMagnitude = 0.3;

        for (ItemEntity itemEntity : items) {
            Vec3d dir = origin.subtract(itemEntity.getPos()).normalize();
            Vec3d finalMotionDirection = pushing ? dir.multiply(-1.0) : dir;
            Vec3d addedMotion = finalMotionDirection.multiply(forceMagnitude);

            itemEntity.setVelocity(itemEntity.getVelocity().add(addedMotion));

            itemEntity.setNoGravity(false);
            itemEntity.setPickupDelay(0);
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var component = stack.get(ModDataComponents.BOUND_ITEM);
        if (component != null) {
            tooltip.add(Text.literal("Bound to: ")
                    .append(component.stack().getName())
                    .formatted(Formatting.GRAY));
        } else {
            tooltip.add(Text.literal("Not bound to any item").formatted(Formatting.DARK_GRAY));
        }
    }
}

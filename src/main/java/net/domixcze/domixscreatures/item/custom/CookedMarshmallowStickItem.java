package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CookedMarshmallowStickItem extends Item {
    private static final int MAX_COOKING_TIME = 10 * 20;
    private static final int COOK_TIME = 5 * 20;
    private static final double RAYTRACE_RANGE = 5.0D;

    private boolean cookingMode = false;

    public CookedMarshmallowStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        HitResult hit = user.raycast(RAYTRACE_RANGE, 0.0F, false);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof CampfireBlock && state.get(CampfireBlock.LIT)) {
                cookingMode = true;
                user.setCurrentHand(hand);
                return TypedActionResult.consume(stack);
            }
        }

        cookingMode = false;
        user.setCurrentHand(hand);
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!cookingMode) return;

        if (!(user instanceof PlayerEntity player)) return;
        HitResult hit = player.raycast(RAYTRACE_RANGE, 0.0F, false);

        if (hit.getType() != HitResult.Type.BLOCK) {
            player.stopUsingItem();
            return;
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof CampfireBlock) || !state.get(CampfireBlock.LIT)) {
            player.stopUsingItem();
            return;
        }

        int elapsed = MAX_COOKING_TIME - remainingUseTicks;

        if (elapsed >= COOK_TIME) {
            if (!world.isClient) {
                ItemStack burnt = new ItemStack(ModItems.BURNT_MARSHMALLOW_STICK, stack.getCount());
                player.setStackInHand(player.getActiveHand(), burnt);
                world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
            player.stopUsingItem();
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return cookingMode ? MAX_COOKING_TIME : 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return cookingMode ? UseAction.BLOCK : UseAction.EAT;
    }
}
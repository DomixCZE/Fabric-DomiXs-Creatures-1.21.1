package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MarshmallowStickItem extends Item {
    private static final int MAX_COOKING_TIME = 15 * 20;
    private static final int COOK_TIME = 10 * 20;
    private static final double RAYTRACE_RANGE = 5.0D;

    public MarshmallowStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult hit = user.raycast(RAYTRACE_RANGE, 0.0F, false);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        BlockPos pos = ((BlockHitResult) hit).getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof CampfireBlock) || !state.get(CampfireBlock.LIT)) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
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
                ItemStack cooked = new ItemStack(ModItems.COOKED_MARSHMALLOW_STICK, stack.getCount());
                player.setStackInHand(player.getActiveHand(), cooked);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
            player.stopUsingItem();
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return MAX_COOKING_TIME;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }
}
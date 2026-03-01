package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EraserItem extends Item {
    public EraserItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getWorld().isClient && target.getType().isIn(ModTags.EntityTypes.ERASABLE)) {
            target.damage(target.getDamageSources().playerAttack((PlayerEntity) attacker), Float.MAX_VALUE);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity.getType().isIn(ModTags.EntityTypes.ERASABLE)) {
            if (!user.getWorld().isClient) {
                user.getWorld().playSound(null, entity.getBlockPos(),
                        SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);

                entity.discard();
            }
            return ActionResult.success(user.getWorld().isClient);
        }
        return ActionResult.PASS;
    }
}
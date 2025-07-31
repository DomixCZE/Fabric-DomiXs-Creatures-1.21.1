package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EraserItem extends Item {
    public EraserItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.getWorld().isClient && target.getType().isIn(ModTags.EntityTypes.ERASABLE)) {
            target.discard();
        }
        return super.postHit(stack, target, attacker);
    }
}
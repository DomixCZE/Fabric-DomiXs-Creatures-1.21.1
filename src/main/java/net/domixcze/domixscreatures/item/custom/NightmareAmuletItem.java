package net.domixcze.domixscreatures.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class NightmareAmuletItem extends Item {
    public NightmareAmuletItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (entity instanceof PlayerEntity player) {
                if (player.getStackInHand(Hand.MAIN_HAND) == stack || player.getStackInHand(Hand.OFF_HAND) == stack) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 5, 0, false, false, false));
                }
            }
        }
    }
}

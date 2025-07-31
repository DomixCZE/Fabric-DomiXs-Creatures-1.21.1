package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CoconutSliceItem extends Item {
    public CoconutSliceItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack resultStack = super.finishUsing(stack, world, user);

        if (!world.isClient) {
            if (user instanceof PlayerEntity player) {
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                if (!player.getAbilities().creativeMode) {
                    if (resultStack.isEmpty()) {
                        resultStack = new ItemStack(ModItems.COCONUT_SHELL);
                    } else {
                        player.getInventory().insertStack(new ItemStack(ModItems.COCONUT_SHELL));
                    }
                }
            }

            List<StatusEffectInstance> candidateEffects = new ArrayList<>();

            for (StatusEffectInstance effect : user.getStatusEffects()) {
                boolean isBeneficial = effect.getEffectType().value().isBeneficial();

                if (isBeneficial && ModConfig.INSTANCE.enableCoconutPositiveEffectRemoval) {
                    candidateEffects.add(effect);
                } else if (!isBeneficial && ModConfig.INSTANCE.enableCoconutNegativeEffectRemoval) {
                    candidateEffects.add(effect);
                }
            }

            if (!candidateEffects.isEmpty()) {
                StatusEffectInstance effectToRemove = candidateEffects.get(world.random.nextInt(candidateEffects.size()));
                user.removeStatusEffect(effectToRemove.getEffectType());
            }
        }
        return resultStack;
    }
}
package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class DeathWhistleItem extends Item {

    public DeathWhistleItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        player.setCurrentHand(hand);

        if (!world.isClient) {
            var component = stack.get(ModDataComponents.DEATH_WHISTLE);
            if (component != null) {
                Identifier soundId = component.soundId();
                SoundEvent sound = Registries.SOUND_EVENT.get(soundId);

                if (sound != null) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }

                @SuppressWarnings("unchecked")
                EntityType<? extends LivingEntity> entityType =
                        (EntityType<? extends LivingEntity>) Registries.ENTITY_TYPE.get(component.entityTypeId());

                Box area = player.getBoundingBox().expand(16);
                var targets = world.getEntitiesByType(entityType, area, e -> true);

                for (LivingEntity target : targets) {
                    target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
                }

            } else {
                player.sendMessage(Text.literal("This whistle holds no entity."), true);
            }
        }

        player.getItemCooldownManager().set(this, 200);
        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return TypedActionResult.consume(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 500;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal("EXPERIMENTAL!").formatted(Formatting.RED));

        if (Screen.hasShiftDown()) {
            tooltip.add(Text.literal("This item is experimental and may be removed!"));
            tooltip.add(Text.literal("To use it, place it in your off-hand and kill a mob."));
            tooltip.add(Text.literal("The item will store that mob's death sound."));
            tooltip.add(Text.literal("Right-click to play the stored sound,"));
            tooltip.add(Text.literal("causing nearby mobs of the same type to gain the Weakness effect."));
        } else {
            tooltip.add(Text.literal("Hold SHIFT for more info."));
        }

        var component = stack.get(ModDataComponents.DEATH_WHISTLE);
        Optional<TextColor> color = TextColor.parse("#6A5ACD").result();

        if (component != null) {
            var entityType = Registries.ENTITY_TYPE.get(component.entityTypeId());
            tooltip.add(Text.translatable(entityType.getTranslationKey())
                    .setStyle(Style.EMPTY.withColor(color.orElse(TextColor.fromFormatting(net.minecraft.util.Formatting.DARK_PURPLE)))));
        } else {
            tooltip.add(Text.literal("No entity stored")
                    .setStyle(Style.EMPTY.withColor(color.orElse(TextColor.fromFormatting(net.minecraft.util.Formatting.DARK_PURPLE)))));
        }
    }
}
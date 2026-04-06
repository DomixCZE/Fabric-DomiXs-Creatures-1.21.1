package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.entity.custom.QuillProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.List;

public class QuillItem extends Item implements ProjectileItem {

    public QuillItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        QuillProjectileEntity projectile = new QuillProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ());

        ModDataComponents.CoatedQuillComponent effect = stack.get(ModDataComponents.COATED_QUILL);

        if (effect != null) {
            projectile.setDartEffect(effect);
        }

        return projectile;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var dartEffect = stack.get(ModDataComponents.COATED_QUILL);
        if (dartEffect != null) {
            MutableText effectText = Registries.STATUS_EFFECT.getEntry(dartEffect.effectId())
                    .map(entry -> Text.translatable(entry.value().getTranslationKey()))
                    .orElse(Text.literal(dartEffect.effectId().getPath()).copy());

            if (dartEffect.amplifier() > 0) {
                effectText.append(" ").append(Text.translatable("potion.potency." + dartEffect.amplifier()));
            }

            tooltip.add(Text.translatable("domixs-creatures.tooltip.quill.effect")
                    .formatted(Formatting.GOLD)
                    .append(effectText.formatted(Formatting.YELLOW)));

            int seconds = dartEffect.duration() / 20;
            tooltip.add(Text.translatable("domixs-creatures.tooltip.quill.duration")
                    .formatted(Formatting.GOLD)
                    .append(Text.literal(seconds + "s").formatted(Formatting.YELLOW)));
        }

        super.appendTooltip(stack, context, tooltip, type);
    }
}

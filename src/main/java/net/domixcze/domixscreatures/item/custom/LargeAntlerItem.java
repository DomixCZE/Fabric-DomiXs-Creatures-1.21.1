package net.domixcze.domixscreatures.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class LargeAntlerItem extends Item {
    public LargeAntlerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Optional<TextColor> color = TextColor.parse("#6A5ACD").result();
        if (color.isPresent()) {
            tooltip.add(Text.translatable("domixs-creatures.tooltip.large_antler")
                    .setStyle(Style.EMPTY.withColor(color.get())));
        } else {
            System.err.println("Error parsing color: #6A5ACD");
            tooltip.add(Text.translatable("domixs-creatures.tooltip.large_antler")
                    .setStyle(Style.EMPTY.withColor(Formatting.DARK_PURPLE))); // Fallback color
        }
    }
}
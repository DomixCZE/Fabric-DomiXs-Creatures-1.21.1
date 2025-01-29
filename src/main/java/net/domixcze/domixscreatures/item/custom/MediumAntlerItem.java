package net.domixcze.domixscreatures.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.world.World;

import java.util.List;

public class MediumAntlerItem extends Item {
    public MediumAntlerItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.translatable("domixs-creatures.tooltip.medium_antler")
                .styled(style -> style.withColor(TextColor.parse("#6A5ACD"))));

    }
}
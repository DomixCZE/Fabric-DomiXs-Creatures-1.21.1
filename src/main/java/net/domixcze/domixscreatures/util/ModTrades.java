package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;

public class ModTrades {
    public static void registerCustomTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(ModBlocks.BARNACLE_BLOCK, 4),
                            new ItemStack(Items.EMERALD, 2),
                            15, 3, 0.075f));
                });
    }
}

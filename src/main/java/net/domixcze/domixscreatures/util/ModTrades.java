package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Optional;

public class ModTrades {
    public static void registerCustomTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FISHERMAN, 1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModBlocks.BARNACLE_BLOCK, 4),
                            new ItemStack(Items.EMERALD, 2),
                            15, 3, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.SHARK_TOOTH, 3),
                            new ItemStack(Items.EMERALD, 12),
                            10, 5, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.RAW_CARP, 3),
                            new ItemStack(Items.EMERALD, 1),
                            8, 3, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.COOKED_CARP, 4),
                            new ItemStack(Items.EMERALD, 2),
                            8, 5, 0.095f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(Items.EMERALD, 1),
                            new ItemStack(ModItems.RAW_CARP, 3),
                            16, 3, 0.075f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(Items.EMERALD, 1),
                            Optional.of(new TradedItem(ModItems.RAW_CARP, 3)),
                            new ItemStack(ModItems.COOKED_CARP, 3),
                            8, 5, 0.095f));
                });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 1,
                factories -> {

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(Items.EMERALD, 2),
                            new ItemStack(ModItems.SALT, 4),
                            24, 2, 0.030f));

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.RAW_DEER_VENISON, 1),
                            new ItemStack(Items.EMERALD, 2),
                            16, 3, 0.065f));
                });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 2,
                factories -> {

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.COOKED_DEER_VENISON, 1),
                            new ItemStack(Items.EMERALD, 4),
                            8, 5, 0.065f));
                });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 3,
                factories -> {

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.RAW_BISON_MEAT, 1),
                            new ItemStack(Items.EMERALD, 3),
                            16, 4, 0.065f));
                });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.BUTCHER, 4,
                factories -> {

                    factories.add((entity, random) -> new TradeOffer(
                            new TradedItem(ModItems.COOKED_BISON_MEAT, 1),
                            new ItemStack(Items.EMERALD, 5),
                            8, 6, 0.065f));
                });

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER, 1, factories -> {
            if (isChristmas()) {
                factories.add((entity, random) -> new TradeOffer(
                        new TradedItem(Items.EMERALD, 3),
                        new ItemStack(ModItems.PENGUIN_SPAWN_EGG, 1),
                        15, 1, 0.030f
                ));
            }
        });
    }

    private static boolean isChristmas() {
        LocalDate today = LocalDate.now();
        MonthDay start = MonthDay.of(12, 15);
        MonthDay end = MonthDay.of(12, 31);
        MonthDay todayMD = MonthDay.from(today);
        return !todayMD.isBefore(start) && !todayMD.isAfter(end);
    }
}
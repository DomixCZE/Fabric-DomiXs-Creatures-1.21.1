package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ModCompostables {
    public static void register() {
        Map<String, Float> compostables = Map.ofEntries(
                Map.entry("sawdust", 0.2F),
                Map.entry("bark", 0.3F),
                Map.entry("palm_sapling", 0.3F),
                Map.entry("palm_leaves", 0.3F),
                Map.entry("spectral_leaves", 0.3F),
                Map.entry("spectral_sapling", 0.3F),
                Map.entry("spectral_fruit", 0.3F),
                Map.entry("banana", 0.3F),
                Map.entry("coconut_slice", 0.3F),
                Map.entry("coconut_shell", 0.3F),
                Map.entry("water_lily", 0.4F),
                Map.entry("coconut_block", 0.5F),
                Map.entry("truffle", 0.5F),
                Map.entry("honeyed_apple", 0.65F),
                Map.entry("mud_blossom_seed", 1.0F)
        );

        for (Map.Entry<String, Float> entry : compostables.entrySet()) {
            Item item = Registries.ITEM.get(Identifier.of(DomiXsCreatures.MOD_ID, entry.getKey()));
            ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item, entry.getValue());
        }
    }
}
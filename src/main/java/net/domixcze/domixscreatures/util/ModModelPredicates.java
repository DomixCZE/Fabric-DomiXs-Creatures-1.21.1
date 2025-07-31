package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class ModModelPredicates {
    public static void registerModPredicates() {
        ModelPredicateProviderRegistry.register(ModItems.CONCH_SHELL, Identifier.of("blowing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
    }
}
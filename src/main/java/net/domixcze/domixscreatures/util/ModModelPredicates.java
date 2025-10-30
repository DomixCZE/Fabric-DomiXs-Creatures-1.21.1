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

        ModelPredicateProviderRegistry.register(ModItems.DEATH_WHISTLE, Identifier.of("blowing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );


        ModelPredicateProviderRegistry.register(ModItems.MARSHMALLOW_STICK, Identifier.of("cooking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.COOKED_MARSHMALLOW_STICK, Identifier.of("cooking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );


        ModelPredicateProviderRegistry.register(ModItems.COPPER_TRUMPET, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.COPPER_TRUMPET_EXPOSED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.COPPER_TRUMPET_WEATHERED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.COPPER_TRUMPET_OXIDIZED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.WAXED_COPPER_TRUMPET, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.WAXED_COPPER_TRUMPET_EXPOSED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.WAXED_COPPER_TRUMPET_WEATHERED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.WAXED_COPPER_TRUMPET_OXIDIZED, Identifier.of("playing"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
    }
}
package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.item.custom.GlassJarItem;
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

        ModelPredicateProviderRegistry.register(ModItems.GLASS_JAR, Identifier.of("fill_level"),
                (stack, world, entity, seed) -> {
                    ModDataComponents.JarContentsComponent contents = stack.get(ModDataComponents.JAR_CONTENTS);
                    if (contents == null || contents.entities().isEmpty()) return 0f;
                    return (float) contents.entities().size() / GlassJarItem.MAX_CAPACITY;
                });

        ModelPredicateProviderRegistry.register(ModItems.GLASS_JAR, Identifier.of("is_termite"),
                (stack, world, entity, seed) -> {
                    ModDataComponents.JarContentsComponent contents = stack.get(ModDataComponents.JAR_CONTENTS);
                    if (contents == null) return 0f;
                    return contents.type().getPath().contains("termite") ? 1.0f : 0.0f;
                });

        ModelPredicateProviderRegistry.register(ModItems.GLASS_JAR, Identifier.of("is_ant"),
                (stack, world, entity, seed) -> {
                    ModDataComponents.JarContentsComponent contents = stack.get(ModDataComponents.JAR_CONTENTS);
                    if (contents == null) return 0f;
                    return contents.type().getPath().contains("ant") ? 1.0f : 0.0f;
                });

        ModelPredicateProviderRegistry.register(ModItems.MARSHMALLOW_STICK, Identifier.of("cooking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );
        ModelPredicateProviderRegistry.register(ModItems.COOKED_MARSHMALLOW_STICK, Identifier.of("cooking"),
                (itemStack, clientWorld, livingEntity, seed) -> livingEntity != null
                        && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(ModItems.COMMAND_FLUTE, Identifier.of("playing"),
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
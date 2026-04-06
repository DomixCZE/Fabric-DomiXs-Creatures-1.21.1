package net.domixcze.domixscreatures.enchantment;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.value.RemoveBinomialEnchantmentEffect;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> RECOVERY =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(DomiXsCreatures.MOD_ID, "recovery"));

    public static void bootstrap(Registerable<Enchantment> registerable) {
        var enchantments = registerable.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        var items = registerable.getRegistryLookup(RegistryKeys.ITEM);

        register(registerable, RECOVERY, Enchantment.builder(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.BLOWGUN_ENCHANTABLE),
                        5,
                        2,
                        Enchantment.leveledCost(10, 10),
                        Enchantment.leveledCost(50, 10),
                        2,
                        AttributeModifierSlot.HAND))
                .addEffect(EnchantmentEffectComponentTypes.AMMO_USE,
                        new RemoveBinomialEnchantmentEffect(
                                EnchantmentLevelBasedValue.linear(0.1f, 0.1f) // level 1 = 10% chance level 2 = 20% chance
                        ))
        );
    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }
}

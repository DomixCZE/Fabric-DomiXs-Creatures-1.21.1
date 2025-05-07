package net.domixcze.domixscreatures.potion;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModPotions {

    public static final RegistryEntry<Potion> ECHOLOCATION_POTION = registerPotion("echolocation_potion",
            new Potion(new StatusEffectInstance(ModEffects.ECHOLOCATION, 200, 0)));

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(DomiXsCreatures.MOD_ID, name), potion);
    }

    public static void registerPotions() {
        DomiXsCreatures.LOGGER.info("Registering Mod Potions for " + DomiXsCreatures.MOD_ID);
    }
}
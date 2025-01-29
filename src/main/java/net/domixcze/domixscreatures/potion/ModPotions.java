package net.domixcze.domixscreatures.potion;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.mixin.BrewingRecipeRegistryMixin;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotions {
    public static Potion ECHOLOCATION_POTION;

    public static Potion registerPotion(String name) {
        return Registry.register(Registries.POTION, new Identifier(DomiXsCreatures.MOD_ID, name),
                new Potion(new StatusEffectInstance(ModEffects.ECHOLOCATION, 200, 0)));
    }

    public static void registerPotions() {
        ECHOLOCATION_POTION = registerPotion("echolocation_potion");
        registerPotionRecipes();
    }

    private static void registerPotionRecipes() {
        BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, ModItems.SPECTRAL_BAT_EAR, ModPotions.ECHOLOCATION_POTION);
    }
}
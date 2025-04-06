package net.domixcze.domixscreatures.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static final FoodComponent SPECTRAL_FRUIT = new FoodComponent.Builder().hunger(-3).saturationModifier(0.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 250), 1.0f).build();

    public static final FoodComponent GOLDFISH = new FoodComponent.Builder().hunger(2).saturationModifier(0.30f)
            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000), 1.0f).build();

    public static final FoodComponent WORM = new FoodComponent.Builder().hunger(1).saturationModifier(0.10f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200), 0.3f).build();
}
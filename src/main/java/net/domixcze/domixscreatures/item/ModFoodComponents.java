package net.domixcze.domixscreatures.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent SPECTRAL_FRUIT = new FoodComponent.Builder().nutrition(-3).saturationModifier(0.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 250), 1.0f).build();

    public static final FoodComponent HONEYED_APPLE = new FoodComponent.Builder().nutrition(6).saturationModifier(0.40f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100), 1.0f).build();

    public static final FoodComponent BANANA = new FoodComponent.Builder().nutrition(3).saturationModifier(0.20f)
            .build();

    public static final FoodComponent COCONUT_SLICE = new FoodComponent.Builder().nutrition(2).saturationModifier(0.20f)
            .build();

    public static final FoodComponent COOKED_MARSHMALLOW = new FoodComponent.Builder().nutrition(2).saturationModifier(0.50f)
            .build();
    public static final FoodComponent BURNT_MARSHMALLOW = new FoodComponent.Builder().nutrition(1).saturationModifier(-0.50f)
            .build();

    public static final FoodComponent GOLDFISH = new FoodComponent.Builder().nutrition(2).saturationModifier(0.30f)
            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000), 1.0f).build();

    public static final FoodComponent WORM = new FoodComponent.Builder().nutrition(1).saturationModifier(0.10f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 200), 0.3f).build();

    public static final FoodComponent BISON_MEAT = new FoodComponent.Builder().nutrition(3).saturationModifier(0.50f)
            .build();

    public static final FoodComponent COOKED_BISON_MEAT = new FoodComponent.Builder().nutrition(8).saturationModifier(2.0f)
            .build();

    public static final FoodComponent DEER_VENISON = new FoodComponent.Builder().nutrition(2).saturationModifier(0.20f)
            .build();

    public static final FoodComponent COOKED_DEER_VENISON = new FoodComponent.Builder().nutrition(5).saturationModifier(1.0f)
            .build();

    public static final FoodComponent EEL_MEAT = new FoodComponent.Builder().nutrition(2).saturationModifier(0.20f)
            .build();

    public static final FoodComponent COOKED_EEL_MEAT = new FoodComponent.Builder().nutrition(4).saturationModifier(0.80f)
            .build();
}
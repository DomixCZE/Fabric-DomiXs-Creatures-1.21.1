package net.domixcze.domixscreatures.effect;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEffects {
    public static StatusEffect DEAFEN;
    public static StatusEffect ECHOLOCATION;
    public static StatusEffect BLEEDING;
    public static StatusEffect ELECTRIFIED;

    public static StatusEffect registerDeafenStatusEffect(String name) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(DomiXsCreatures.MOD_ID, name),
                new DeafenEffect(StatusEffectCategory.HARMFUL, 3124687));
    }

    public static StatusEffect registerEcholocationStatusEffect(String name) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(DomiXsCreatures.MOD_ID, name),
                new EcholocationEffect(StatusEffectCategory.BENEFICIAL, 14124217));
    }

    public static StatusEffect registerBleedingStatusEffect(String name) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(DomiXsCreatures.MOD_ID, name),
                new BleedingEffect(StatusEffectCategory.HARMFUL, 13898772));
    }

    public static StatusEffect registerElectrifiedStatusEffect(String name) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(DomiXsCreatures.MOD_ID, name),
                new ElectrifiedEffect(StatusEffectCategory.HARMFUL, 13099489));
    }

    public static void registerEffects() {
        DEAFEN = registerDeafenStatusEffect("deafen");
        ECHOLOCATION = registerEcholocationStatusEffect("echolocation");
        BLEEDING = registerBleedingStatusEffect("bleeding");
        ELECTRIFIED = registerElectrifiedStatusEffect("electrified");
    }
}
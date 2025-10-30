package net.domixcze.domixscreatures.effect;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModEffects {

    public static final RegistryEntry<StatusEffect> DEAFEN = registerStatusEffect("deafen",
            new DeafenEffect(StatusEffectCategory.HARMFUL, 3124687));

    public static final RegistryEntry<StatusEffect> ECHOLOCATION = registerStatusEffect("echolocation",
            new EcholocationEffect(StatusEffectCategory.BENEFICIAL, 14124217));

    public static final RegistryEntry<StatusEffect> BLEEDING = registerStatusEffect("bleeding",
            new BleedingEffect(StatusEffectCategory.HARMFUL, 13898772));

    public static final RegistryEntry<StatusEffect> ELECTRIFIED = registerStatusEffect("electrified",
            new ElectrifiedEffect(StatusEffectCategory.HARMFUL, 13099489));

    public static final RegistryEntry<StatusEffect> OCEAN_BLESSING = registerStatusEffect("ocean_blessing",
            new OceanBlessingEffect(StatusEffectCategory.BENEFICIAL, 13099489));

    private static RegistryEntry<StatusEffect> registerStatusEffect(String name, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(DomiXsCreatures.MOD_ID, name), statusEffect);
    }

    public static void registerEffects() {
        DomiXsCreatures.LOGGER.info("Registering Mod Effects for " + DomiXsCreatures.MOD_ID);
    }
}
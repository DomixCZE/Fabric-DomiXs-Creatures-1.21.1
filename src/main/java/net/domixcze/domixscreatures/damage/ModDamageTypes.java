package net.domixcze.domixscreatures.damage;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> BLEEDING = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "bleeding"));

    public static final RegistryKey<DamageType> ELECTRIC = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE, new Identifier(DomiXsCreatures.MOD_ID, "electric"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        RegistryEntry<DamageType> damageTypeEntry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(key)
                .orElseThrow(() -> new IllegalStateException("DamageType not found: " + key.getValue()));

        return new DamageSource(damageTypeEntry);
    }
}
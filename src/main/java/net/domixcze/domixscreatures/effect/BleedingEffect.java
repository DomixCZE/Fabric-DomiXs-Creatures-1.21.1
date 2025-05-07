package net.domixcze.domixscreatures.effect;

import net.domixcze.domixscreatures.damage.ModDamageTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BleedingEffect extends StatusEffect {
    protected BleedingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.BLEEDING), 1.0F);
        }
        return super.applyUpdateEffect(entity, amplifier);
    }
}
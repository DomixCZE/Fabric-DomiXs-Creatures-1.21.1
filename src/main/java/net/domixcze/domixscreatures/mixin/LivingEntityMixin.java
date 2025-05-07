package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<? extends StatusEffect> effect);

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void cancelHeal(float amount, CallbackInfo callbackInfo) {
        if (this.hasStatusEffect(ModEffects.BLEEDING)) {
            callbackInfo.cancel();
        }
    }
}
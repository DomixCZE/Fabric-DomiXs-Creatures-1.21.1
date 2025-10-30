package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.item.custom.DeathWhistleItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<? extends StatusEffect> effect);

    @Shadow
    protected abstract SoundEvent getDeathSound();

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void cancelHeal(float amount, CallbackInfo ci) {
        if (this.hasStatusEffect(ModEffects.BLEEDING)) {
            ci.cancel();
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void captureDeathSound(DamageSource source, CallbackInfo ci) {
        if (!(source.getAttacker() instanceof PlayerEntity player)) return;

        ItemStack offhand = player.getOffHandStack();
        if (!(offhand.getItem() instanceof DeathWhistleItem)) return;

        SoundEvent sound = this.getDeathSound();
        if (sound != null) {
            offhand.set(ModDataComponents.DEATH_WHISTLE,
                    new ModDataComponents.DeathWhistleComponent(
                            Registries.SOUND_EVENT.getId(sound),
                            Registries.ENTITY_TYPE.getId(((LivingEntity) (Object) this).getType())
                    ));
        }
    }
}
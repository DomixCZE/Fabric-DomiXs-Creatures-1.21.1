package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.item.custom.DeathWhistleItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<? extends StatusEffect> effect);

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow protected abstract SoundEvent getDeathSound();

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void domixscreatures$cancelPoisonWithMask(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (effect.getEffectType().equals(StatusEffects.POISON)) {
            ItemStack mask = this.getEquippedStack(EquipmentSlot.HEAD);
            if (mask.isOf(ModItems.SHAMAN_MASK)) {
                LivingEntity entity = (LivingEntity) (Object) this;
                if (!entity.getWorld().isClient) {
                    mask.damage(1, entity, EquipmentSlot.HEAD);
                }
                cir.setReturnValue(false);
            }
        }
    }

    @ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true)
    private StatusEffectInstance domixscreatures$reducePoisonDuration(StatusEffectInstance effect) {
        if (effect.getEffectType().equals(StatusEffects.POISON) && this.hasFullWyvernSet()) {
            return new StatusEffectInstance(
                    effect.getEffectType(),
                    effect.getDuration() / 2,
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.shouldShowParticles(),
                    effect.shouldShowIcon()
            );
        }
        return effect;
    }

    private boolean hasFullWyvernSet() {
        return this.getEquippedStack(EquipmentSlot.HEAD).isOf(ModItems.WYVERN_SCALE_HELMET) &&
                this.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.WYVERN_SCALE_CHESTPLATE) &&
                this.getEquippedStack(EquipmentSlot.LEGS).isOf(ModItems.WYVERN_SCALE_LEGGINGS) &&
                this.getEquippedStack(EquipmentSlot.FEET).isOf(ModItems.WYVERN_SCALE_BOOTS);
    }

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void domixscreatures$cancelHeal(float amount, CallbackInfo ci) {
        if (this.hasStatusEffect(ModEffects.BLEEDING)) {
            ci.cancel();
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void domixscreatures$captureDeathSound(DamageSource source, CallbackInfo ci) {
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
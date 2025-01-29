package net.domixcze.domixscreatures.mixin;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Final
    private SoundSystem soundSystem;

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.hasStatusEffect(ModEffects.DEAFEN)) {
            ci.cancel();
        }
    }
}

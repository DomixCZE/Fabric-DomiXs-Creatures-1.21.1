package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.domixcze.domixscreatures.sound.CaveWyvernBreathSoundInstance;
import net.minecraft.client.MinecraftClient;

public class CaveWyvernSoundHelper {
    public static void playBreathSound(CaveWyvernEntity wyvern) {
        MinecraftClient.getInstance().getSoundManager().play(new CaveWyvernBreathSoundInstance(wyvern));
    }
}
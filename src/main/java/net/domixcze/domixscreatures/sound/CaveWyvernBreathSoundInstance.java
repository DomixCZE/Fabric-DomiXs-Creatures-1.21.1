package net.domixcze.domixscreatures.sound;

import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class CaveWyvernBreathSoundInstance extends MovingSoundInstance {
    private final CaveWyvernEntity wyvern;
    private int fadeOutTicks = 0;
    private static final int FADE_DURATION = 15;

    public CaveWyvernBreathSoundInstance(CaveWyvernEntity wyvern) {
        super(ModSounds.CAVE_WYVERN_BREATH, SoundCategory.HOSTILE, SoundInstance.createRandom());
        this.wyvern = wyvern;
        this.repeat = false;
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (this.wyvern.isRemoved()) {
            this.setDone();
            return;
        }

        if (this.wyvern.isBreathingPoison() && fadeOutTicks == 0) {
            this.x = (float)this.wyvern.getX();
            this.y = (float)this.wyvern.getY();
            this.z = (float)this.wyvern.getZ();
        } else {
            fadeOutTicks++;
            this.volume = Math.max(0, 1.0F - ((float)fadeOutTicks / FADE_DURATION));

            if (this.volume <= 0) {
                this.setDone();
            }
        }
    }
}
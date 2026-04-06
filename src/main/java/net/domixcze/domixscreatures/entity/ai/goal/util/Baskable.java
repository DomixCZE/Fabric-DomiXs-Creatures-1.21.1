package net.domixcze.domixscreatures.entity.ai.goal.util;

import net.minecraft.world.World;

public interface Baskable {
    boolean isBasking();
    void setBasking(boolean basking);
    int getBaskingCooldown();
    void setBaskingCooldown(int ticks);

    default boolean isNotBaskingTime(World world, int start, int end) {
        long time = world.getTimeOfDay() % 24000;
        return time < start || time > end || !world.isDay() || world.isRaining();
    }
}
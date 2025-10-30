package net.domixcze.domixscreatures.entity.ai;

public interface SnowLayerable {
    boolean hasSnowLayer();
    void setHasSnowLayer(boolean hasSnow);

    int getSnowTicks();
    void setSnowTicks(int ticks);
    int getSnowMeltTimer();
    void setSnowMeltTimer(int timer);
}
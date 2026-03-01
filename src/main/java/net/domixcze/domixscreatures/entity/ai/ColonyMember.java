package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.util.math.BlockPos;

public interface ColonyMember {
    void setHomePos(BlockPos pos);
    BlockPos getHomePos();
}
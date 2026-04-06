package net.domixcze.domixscreatures.entity.ai.goal.util;

import net.minecraft.util.math.BlockPos;

public interface ColonyMember {
    void setHomePos(BlockPos pos);
    BlockPos getHomePos();
}
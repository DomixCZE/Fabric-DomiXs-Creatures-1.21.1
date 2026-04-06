package net.domixcze.domixscreatures.entity.ai.brain.util;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface TameableBird extends Bird {
    boolean isTamed();

    boolean isSitting();

    @Nullable
    LivingEntity getOwner();

    default float getGroundFollowOwnerSpeed() {
        return 1.0F;
    }

    default float getAirFollowOwnerSpeed() {
        return 1.2F;
    }

    default double getStartFollowOwnerDistanceSq() {
        return 9.0D;
    }

    default double getStartFlyFollowOwnerDistanceSq() {
        return 100.0D;
    }

    default double getStopFollowOwnerDistanceSq() {
        return 4.0D;
    }

    default double getOwnerTeleportDistanceSq() {
        return 144.0D;
    }
}

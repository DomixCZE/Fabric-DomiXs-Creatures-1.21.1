package net.domixcze.domixscreatures.entity.ai.brain.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public interface Bird {
    boolean isFlying();

    boolean isLanding();

    boolean canStartFlying();

    void startFlying();

    void startLanding();

    void stopFlying();

    int getFlightTicks();

    default int getMinFlightTicks() {
        return 60;
    }

    default boolean shouldStayFlying() {
        return false;
    }

    default boolean canLand() {
        return this.getFlightTicks() >= this.getMinFlightTicks();
    }

    default double getThreatHorizontalRange() {
        return 8.0D;
    }

    default double getThreatVerticalRange() {
        return 4.0D;
    }

    default int getRandomTakeoffInterval() {
        return 220;
    }

    default double getTakeoffHorizontalOffset() {
        return 6.0D;
    }

    default double getTakeoffMinY() {
        return 4.0D;
    }

    default double getTakeoffExtraY() {
        return 3.0D;
    }

    default float getTakeoffSpeed() {
        return 1.25F;
    }

    default int getAirWanderHorizontalRange() {
        return 10;
    }

    default int getAirWanderMinHeightAboveGround() {
        return 4;
    }

    default int getAirWanderMaxHeightAboveGround() {
        return 9;
    }

    default int getAirWanderHeightJitter() {
        return 2;
    }

    default float getAirWanderSpeed() {
        return 1.1F;
    }

    default int getAirWanderTargetMaxTries() {
        return 15;
    }

    default int getAirWanderRetargetMinTicks() {
        return 30;
    }

    default int getAirWanderRetargetMaxTicks() {
        return 60;
    }

    default double getAirWanderReachedDistanceSq() {
        return 6.0D;
    }

    default int getLandingSearchRadius() {
        return 8;
    }

    default int getLandingSearchMaxTries() {
        return 20;
    }

    default int getLandingChanceInterval() {
        return 35;
    }

    default float getLandingSpeed() {
        return 0.85F;
    }

    default List<TagKey<EntityType<?>>> getNonScaryEntityTypeTags() {
        return List.of();
    }

    default boolean isScaredBy(LivingEntity entity) {
        for (TagKey<EntityType<?>> tag : this.getNonScaryEntityTypeTags()) {
            if (entity.getType().isIn(tag)) {
                return false;
            }
        }

        return true;
    }
}
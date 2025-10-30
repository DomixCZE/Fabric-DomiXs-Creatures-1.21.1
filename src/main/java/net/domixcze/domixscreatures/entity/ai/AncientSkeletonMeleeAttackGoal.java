package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.AncientSkeletonEntity;

public class AncientSkeletonMeleeAttackGoal extends ModMeleeAttackGoal<AncientSkeletonEntity> {

    public AncientSkeletonMeleeAttackGoal(AncientSkeletonEntity skeleton, double speed, boolean pauseWhenMobIdle) {
        super(skeleton, speed, pauseWhenMobIdle, 10, "land_controller", null);
    }
}
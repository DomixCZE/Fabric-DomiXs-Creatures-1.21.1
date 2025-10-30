package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.TigerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class TigerMeleeAttackGoal extends ModMeleeAttackGoal<TigerEntity> {
    private final TigerEntity tiger;

    public TigerMeleeAttackGoal(TigerEntity tiger, double speed, boolean pauseWhenMobIdle) {
        super(tiger, speed, pauseWhenMobIdle, 10, "land_controller", null);
        this.tiger = tiger;
    }

    @Override
    public boolean canStart() {
        if (this.tiger.isBaby()) {
            return false;
        }
        if (this.tiger.isSleeping()) {
            return false;
        }

        LivingEntity target = this.tiger.getTarget();
        if (target instanceof TameableEntity tameableTarget) {
            if (tameableTarget.getOwnerUuid() != null && this.tiger.getOwnerUuid() != null && tameableTarget.getOwnerUuid().equals(this.tiger.getOwnerUuid())) {
                return false;
            }
        }
        return super.canStart();
    }
}
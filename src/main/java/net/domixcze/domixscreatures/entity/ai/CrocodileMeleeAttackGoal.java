package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.entity.custom.CrocodileEntity;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CrocodileMeleeAttackGoal extends ModMeleeAttackGoal<CrocodileEntity> {
    private final CrocodileEntity crocodile;

    public CrocodileMeleeAttackGoal(CrocodileEntity crocodile, double speed, boolean pauseWhenMobIdle) {
        super(crocodile, speed, pauseWhenMobIdle, 10, "land_controller", ModSounds.CROCODILE_ATTACK);
        this.crocodile = crocodile;
    }

    @Override
    public boolean canStart() {
        if (this.crocodile.isBaby()) return false;

        LivingEntity target = this.crocodile.getTarget();

        if (target instanceof PlayerEntity player) {
            if (player.isSneaking() && this.crocodile.getAttacker() != player) {
                return false;
            }
        }

        if (this.crocodile.isSleeping()) {
            return false;
        }

        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.crocodile.getTarget();
        return target != null && this.crocodile.isAlive() || super.shouldContinue();
    }
}
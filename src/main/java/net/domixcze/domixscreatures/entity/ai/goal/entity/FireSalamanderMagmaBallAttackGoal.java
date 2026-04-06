package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.domixcze.domixscreatures.entity.custom.MagmaBallEntity;
import net.domixcze.domixscreatures.util.ModGameRules;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class FireSalamanderMagmaBallAttackGoal extends Goal {
    private final FireSalamanderEntity salamander;
    private static final double ATTACK_RANGE = 10.0;

    public FireSalamanderMagmaBallAttackGoal(FireSalamanderEntity salamander) {
        this.salamander = salamander;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!salamander.getWorld().getGameRules().getBoolean(ModGameRules.ENABLE_FIRE_SALAMANDER_MAGMA_ATTACK)) {
            return false;
        }

        LivingEntity target = this.salamander.getTarget();
        return target != null
                && !salamander.isObsidianVariant()
                && salamander.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE
                && salamander.getMagmaBallCooldown() <= 0 && salamander.isOnGround();
    }

    @Override
    public void start() {
        salamander.setMagmaBallChargeTime(0);
        salamander.getNavigation().stop();
        salamander.setCharging(true);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = salamander.getTarget();
        return target != null && salamander.getMagmaBallChargeTime() < 25;
    }

    @Override
    public void tick() {
        LivingEntity target = salamander.getTarget();
        if (target != null) {
            salamander.getLookControl().lookAt(target, 30.0F, 30.0F);
        }

        int charge = salamander.getMagmaBallChargeTime();
        salamander.setMagmaBallChargeTime(charge + 1);

        if (charge + 1 == 25) {
            spawnMagmaBall();
            salamander.setMagmaBallCooldown(400);
        }
    }

    @Override
    public void stop() {
        salamander.setMagmaBallChargeTime(0);
        salamander.setCharging(false);
    }

    private void spawnMagmaBall() {
        Vec3d direction = salamander.getRotationVector();
        MagmaBallEntity magmaBall = new MagmaBallEntity(ModEntities.MAGMA_BALL, salamander.getWorld());

        Vec3d spawnOffset = direction.multiply(0.6);
        magmaBall.setPosition(salamander.getX() + spawnOffset.x,
                salamander.getY(),
                salamander.getZ() + spawnOffset.z);

        magmaBall.setVelocity(direction.multiply(0.5));
        magmaBall.setYaw(salamander.getYaw());

        salamander.getWorld().spawnEntity(magmaBall);
        salamander.setCharging(false);
    }
}
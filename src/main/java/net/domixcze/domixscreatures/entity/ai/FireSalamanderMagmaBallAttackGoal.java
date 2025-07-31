package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.config.ModConfig;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.domixcze.domixscreatures.entity.custom.MagmaBallEntity;
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
        if (!ModConfig.INSTANCE.enableFireSalamanderMagmaBallAttack) {
            return false;
        }

        LivingEntity target = this.salamander.getTarget();
        return target != null
                && !this.salamander.isObsidianVariant()
                && this.salamander.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE
                && this.salamander.getMagmaBallCooldown() <= 0 && this.salamander.isOnGround();
    }

    @Override
    public void start() {
        this.salamander.setMagmaBallChargeTime(0);
        this.salamander.getNavigation().stop();
        this.salamander.setCharging(true);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = this.salamander.getTarget();
        return target != null && this.salamander.getMagmaBallChargeTime() < 25;
    }

    @Override
    public void tick() {
        LivingEntity target = this.salamander.getTarget();
        if (target != null) {
            this.salamander.getLookControl().lookAt(target, 30.0F, 30.0F);
        }

        int charge = this.salamander.getMagmaBallChargeTime();
        this.salamander.setMagmaBallChargeTime(charge + 1);

        if (charge + 1 == 25) {
            spawnMagmaBall();
            this.salamander.setMagmaBallCooldown(400);
        }
    }

    @Override
    public void stop() {
        this.salamander.setMagmaBallChargeTime(0);
        this.salamander.setCharging(false);
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
        this.salamander.setCharging(false);
    }
}
package net.domixcze.domixscreatures.entity.ai.goal;

import net.domixcze.domixscreatures.entity.ai.goal.util.Baskable;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.LightType;

import java.util.EnumSet;

public class BaskGoal<T extends MobEntity & Baskable> extends Goal {
    private final T mob;
    private final int startTime;
    private final int endTime;
    private final int cooldown;

    public BaskGoal(T mob, int startTime, int endTime, int cooldown) {
        this.mob = mob;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cooldown = cooldown;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.mob.isBasking();
    }

    @Override
    public boolean shouldContinue() {
        if (!mob.getNavigation().isIdle()) {
            return false;
        }

        if (mob.getRecentDamageSource() != null) {
            return false;
        }

        if (mob.hasVehicle() || mob.hasPassengers()) {
            return false;
        }

        boolean hasSun = mob.getWorld().getLightLevel(LightType.SKY, mob.getBlockPos().up()) >= 15;

        return hasSun && !mob.isNotBaskingTime(mob.getWorld(), startTime, endTime)
                && !mob.isTouchingWater() && mob.isBasking();
    }

    @Override
    public void tick() {
        mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setBasking(false);
        this.mob.setBaskingCooldown(cooldown);
    }
}

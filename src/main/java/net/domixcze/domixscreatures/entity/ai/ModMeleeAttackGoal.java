package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.EnumSet;

public class ModMeleeAttackGoal<T extends PathAwareEntity & GeoEntity> extends Goal {
    protected final T mob;
    private final double speed;
    private final boolean pauseWhenMobIdle;
    private final int attackDelay; // delay in ticks before the damage is applied. used for syncing the damage with the animation
    private final String controllerName;
    private final SoundEvent attackSound;

    private Path path;
    private LivingEntity target;
    private double targetX, targetY, targetZ;
    private int updateCountdownTicks;
    private int cooldown;
    private int attackProgress;
    private boolean isSwinging;

    private long lastUpdateTime;

    public ModMeleeAttackGoal(T mob, double speed, boolean pauseWhenMobIdle, int attackDelay, String controllerName, SoundEvent attackSound) {
        this.mob = mob;
        this.speed = speed;
        this.pauseWhenMobIdle = pauseWhenMobIdle;
        this.attackDelay = attackDelay;
        this.controllerName = controllerName;
        this.attackSound = attackSound;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        long time = this.mob.getWorld().getTime();
        if (time - this.lastUpdateTime < 20L) return false;
        this.lastUpdateTime = time;

        LivingEntity potentialTarget = this.mob.getTarget();
        if (potentialTarget == null || !potentialTarget.isAlive()) return false;

        if (potentialTarget instanceof PlayerEntity player &&
                (player.isCreative() || player.isSpectator())) return false;

        this.path = this.mob.getNavigation().findPathTo(potentialTarget, 0);
        if (this.path != null || this.mob.isInAttackRange(potentialTarget)) {
            this.target = potentialTarget;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity currentTarget = this.mob.getTarget();
        if (currentTarget == null || !currentTarget.isAlive()) return false;

        if (currentTarget instanceof PlayerEntity player &&
                (player.isCreative() || player.isSpectator())) return false;

        if (!pauseWhenMobIdle) return !this.mob.getNavigation().isIdle();
        return this.mob.isInWalkTargetRange(currentTarget.getBlockPos());
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
        this.mob.setAttacking(true);
        this.updateCountdownTicks = 0;
        this.cooldown = 0;
        this.attackProgress = 0;
        this.isSwinging = false;
    }

    @Override
    public void stop() {
        this.mob.setAttacking(false);
        this.mob.getNavigation().stop();
        this.isSwinging = false;

        LivingEntity currentTarget = this.mob.getTarget();
        if (!(currentTarget instanceof PlayerEntity player &&
                (player.isCreative() || player.isSpectator()))) {
            this.mob.setTarget(null);
        }

        this.target = null;
        this.cooldown = this.getTickCount(20);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive() ||
                (this.target instanceof PlayerEntity player &&
                        (player.isCreative() || player.isSpectator()))) {
            this.stop();
            return;
        }

        this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);

        if ((this.pauseWhenMobIdle || this.mob.getVisibilityCache().canSee(this.target)) && this.updateCountdownTicks <= 0) {
            this.targetX = this.target.getX();
            this.targetY = this.target.getY();
            this.targetZ = this.target.getZ();

            this.updateCountdownTicks = 4 + this.mob.getRandom().nextInt(7);

            double distSq = this.mob.squaredDistanceTo(this.targetX, this.targetY, this.targetZ);
            if (distSq > 1024.0) this.updateCountdownTicks += 10;
            else if (distSq > 256.0) this.updateCountdownTicks += 5;

            if (!this.mob.getNavigation().startMovingTo(this.target, this.speed)) {
                this.updateCountdownTicks += 15;
            }

            this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
        }

        this.cooldown = Math.max(this.cooldown - 1, 0);

        // Don't play the attack anim in water since it looks weird
        if (this.canAttack(this.target) && !this.isSwinging) {
            if (!this.mob.isTouchingWater() && !this.mob.isSubmergedInWater()) {
                this.isSwinging = true;
                this.attackProgress = 0;
                this.mob.triggerAnim(this.controllerName, "attack");
            } else {
                this.mob.tryAttack(this.target);
            }
        }

        if (this.isSwinging) {
            this.attackProgress++;

            if (this.attackProgress == this.attackDelay) {
                if (this.attackSound != null) {
                    this.mob.getWorld().playSound(
                            null,
                            this.mob.getX(),
                            this.mob.getY(),
                            this.mob.getZ(),
                            this.attackSound,
                            SoundCategory.HOSTILE,
                            1.0F,
                            1.0F
                    );
                }
                this.mob.swingHand(Hand.MAIN_HAND);
                this.mob.tryAttack(this.target);
            }

            if (this.attackProgress >= this.attackDelay + 5) {
                this.isSwinging = false;
                this.resetCooldown();
            }
        }
    }

    protected void resetCooldown() {
        this.cooldown = this.getTickCount(20);
    }

    protected boolean canAttack(LivingEntity target) {
        return this.cooldown <= 0
                && target.isAlive()
                && this.mob.isInAttackRange(target)
                && this.mob.getVisibilityCache().canSee(target);
    }

    protected int getTickCount(int ticks) {
        return ticks;
    }
}
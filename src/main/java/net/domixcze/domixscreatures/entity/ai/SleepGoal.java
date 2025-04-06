package net.domixcze.domixscreatures.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Random;

public class SleepGoal extends Goal {
    private final Sleepy sleepyEntity;
    private final MobEntity entity;

    private int cooldown;
    private final int minCooldownTime;
    private final int maxCooldownTime;
    private final Random random = new Random();

    private final boolean sleepsDuringDay;
    private final boolean sleepsDuringNight;

    private final boolean wakesWithPlayerProximity;
    private final boolean wakesWithSneaking;
    private final double wakeUpRange;

    private final boolean wakesDuringThunder;
    private final boolean wakesDuringRain;
    private final boolean wakesWhenRainedOn;
    private final boolean wakesInWater;

    public SleepGoal(Sleepy sleepyEntity, MobEntity entity, boolean sleepsDuringDay, boolean sleepsDuringNight, boolean wakesWithPlayerProximity, boolean wakesWithSneaking, double wakeUpRange, int minCooldownTime, int maxCooldownTime, boolean wakesDuringThunder, boolean wakesDuringRain, boolean wakesWhenRainedOn, boolean wakesInWater) {
        this.sleepyEntity = sleepyEntity;
        this.entity = entity;

        /*Random cooldown before sleeping*/
        this.cooldown = getRandomCooldown();/*chooses a random number between min - max cooldown time*/
        this.minCooldownTime = minCooldownTime;/*minimal cooldown*/
        this.maxCooldownTime = maxCooldownTime;/*maximum cooldown*/

        this.sleepsDuringDay = sleepsDuringDay;
        this.sleepsDuringNight = sleepsDuringNight;

        this.wakesWithPlayerProximity = wakesWithPlayerProximity;/*Determines if the entity wakes up when a player is nearby*/
        this.wakesWithSneaking = wakesWithSneaking;/*Determines if the entity wakes up when the player is in range but sneaking*/
        this.wakeUpRange = wakeUpRange;/*If the player enters this range the entity will wake up*/

        this.wakesDuringThunder = wakesDuringThunder;/*Determines if the entity wakes up during thunderstorms regardless of being under shelter*/
        this.wakesDuringRain = wakesDuringRain;/*Determines if the entity wakes up when it's raining in general*/
        this.wakesWhenRainedOn = wakesWhenRainedOn;/*Determines if the entity wakes up when rain directly touches it*/
        this.wakesInWater = wakesInWater;/*Determines if the entity wakes up when it's in water*/

    }

    private int getRandomCooldown() {
        return random.nextInt(maxCooldownTime - minCooldownTime + 1) + minCooldownTime;
    }

    @Override
    public boolean canStart() {
        boolean shouldSleep = false;

        if (sleepsDuringDay && entity.getWorld().isDay()) {
            shouldSleep = true;
        } else if (sleepsDuringNight && entity.getWorld().isNight()) {
            shouldSleep = true;
        }

        if (wakesWithPlayerProximity) {
            PlayerEntity nearestPlayer = this.entity.getWorld().getClosestPlayer(this.entity, wakeUpRange);
            if (nearestPlayer != null) {
                boolean playerSneaking = nearestPlayer.isSneaking();
                if (!wakesWithSneaking && playerSneaking) {
                    shouldSleep = true;
                } else {
                    shouldSleep = false;
                }
            }
        }

        if (wakesInWater && this.entity.isTouchingWater()) {
            shouldSleep = false;
        }

        if (wakesDuringThunder && this.entity.getWorld().isThundering()) {
            shouldSleep = false;
        }

        if (wakesDuringRain && this.entity.getWorld().isRaining()) {
            shouldSleep = false;
        }

        if (wakesWhenRainedOn && this.entity.isTouchingWaterOrRain()) {
            shouldSleep = false;
        }

        if (shouldSleep) {
            if (cooldown > 0) {
                cooldown--;
            }
            return cooldown <= 0;
        } else {
            cooldown = getRandomCooldown();
            return false;
        }
    }

    @Override
    public void start() {
        sleepyEntity.setSleeping(true);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entity.isSleeping()) {
            this.entity.getNavigation().stop();
        }
    }

    @Override
    public void stop() {
        sleepyEntity.setSleeping(false);
        cooldown = getRandomCooldown();
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }
}
package net.domixcze.domixscreatures.entity.ai.goal;

import net.domixcze.domixscreatures.entity.ai.goal.util.Sleepy;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

import java.util.EnumSet;
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

    private final int particleInterval;
    private int particleTimer = 0;

    private final int maxHitsToWakeUp;
    private int hitCount = 0;
    private boolean hurtThisCycle = false;

    public SleepGoal(Sleepy sleepyEntity, MobEntity entity, int particleInterval, boolean sleepsDuringDay, boolean sleepsDuringNight,
                     boolean wakesWithPlayerProximity, boolean wakesWithSneaking, double wakeUpRange, int minCooldownTime, int maxCooldownTime,
                     boolean wakesDuringThunder, boolean wakesDuringRain, boolean wakesWhenRainedOn, boolean wakesInWater, int maxHitsToWakeUp) {
        this.sleepyEntity = sleepyEntity;
        this.entity = entity;

        this.particleInterval = particleInterval;

        this.cooldown = getRandomCooldown();
        this.minCooldownTime = minCooldownTime;
        this.maxCooldownTime = maxCooldownTime;

        this.sleepsDuringDay = sleepsDuringDay;
        this.sleepsDuringNight = sleepsDuringNight;

        this.wakesWithPlayerProximity = wakesWithPlayerProximity;
        this.wakesWithSneaking = wakesWithSneaking;
        this.wakeUpRange = wakeUpRange;

        this.wakesDuringThunder = wakesDuringThunder;
        this.wakesDuringRain = wakesDuringRain;
        this.wakesWhenRainedOn = wakesWhenRainedOn;
        this.wakesInWater = wakesInWater;

        this.maxHitsToWakeUp = Math.min(maxHitsToWakeUp, 5);

        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
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

        if (wakesWhenRainedOn && this.isBeingRainedOn()) {
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
        this.particleTimer = 0;
        this.hitCount = 0;
        this.hurtThisCycle = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.entity.isSleeping()) {
            this.entity.getNavigation().stop();
            this.entity.getLookControl().tick();

            if (this.entity.hurtTime > 0 && !this.hurtThisCycle) {
                this.hurtThisCycle = true;
                this.hitCount++;

                if (this.hitCount >= this.maxHitsToWakeUp) {
                    this.stop();
                    return;
                }
            }

            if (this.entity.hurtTime <= 0) {
                this.hurtThisCycle = false;
            }

            if (this.particleInterval > 0) {
                particleTimer++;
                if (particleTimer >= particleInterval) {
                    particleTimer = 0;
                    spawnSleepParticle();
                }
            }
        } else {
            particleTimer = 0;
            hitCount = 0;
            hurtThisCycle = false;
        }
    }

    @Override
    public void stop() {
        sleepyEntity.setSleeping(false);
        cooldown = getRandomCooldown();
        particleTimer = 0;
        hitCount = 0;
        hurtThisCycle = false;
    }

    @Override
    public boolean shouldContinue() {
        return this.canStart();
    }

    private boolean isBeingRainedOn() {
        BlockPos blockPos = this.entity.getBlockPos();
        return this.entity.getWorld().isRaining()
                && (this.hasRain(blockPos)
                || this.hasRain(BlockPos.ofFloored(blockPos.getX(), this.entity.getBoundingBox().maxY, blockPos.getZ())));
    }

    private boolean hasRain(BlockPos pos) {
        if (!this.entity.getWorld().isRaining()) {
            return false;
        } else if (!this.entity.getWorld().isSkyVisible(pos)) {
            return false;
        } else if (this.entity.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        } else {
            Biome biome = this.entity.getWorld().getBiome(pos).value();
            return biome.getPrecipitation(pos) == Biome.Precipitation.RAIN;
        }
    }

    Vec3d getHeadPositionWithLookVec(MobEntity entity) {
        Vec3d eyePos = entity.getPos().add(0, entity.getStandingEyeHeight() + 0.1, 0);
        Vec3d lookVec = entity.getRotationVec(1.0F).normalize().multiply(0.4);
        return eyePos.add(lookVec);
    }

    private void spawnSleepParticle() {
        if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
            Vec3d headPos = getHeadPositionWithLookVec(entity);
            serverWorld.spawnParticles(
                    ModParticles.SLEEP,
                    headPos.x, headPos.y, headPos.z,
                    2,
                    0.1, 0.05, 0.1,
                    0
            );
        }
    }
}

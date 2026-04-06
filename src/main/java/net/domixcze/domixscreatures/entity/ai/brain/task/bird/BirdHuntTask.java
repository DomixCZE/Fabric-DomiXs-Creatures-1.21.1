package net.domixcze.domixscreatures.entity.ai.brain.task.bird;

import net.domixcze.domixscreatures.entity.ai.brain.util.TameableBird;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BirdHuntTask<T extends PathAwareEntity & TameableBird> extends MultiTickTask<T> {

    private final TagKey<EntityType<?>> huntTag;
    private final boolean canHuntWhenTamed;
    private final double huntRangeXZ;
    private final double huntRangeY;
    private final float flyingSpeed;
    private final float groundSpeed;
    private final int attackCooldownTicks;

    private int attackCooldown;

    public BirdHuntTask(TagKey<EntityType<?>> huntTag) {
        this(huntTag, false, 10.0D, 4.0D, 1.1F, 1.0F, 20);
    }

    public BirdHuntTask(TagKey<EntityType<?>> huntTag,
                        boolean canHuntWhenTamed,
                        double huntRangeXZ,
                        double huntRangeY,
                        float flyingSpeed,
                        float groundSpeed,
                        int attackCooldownTicks) {
        super(Map.of(
                MemoryModuleType.ATTACK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED
        ));

        this.huntTag = huntTag;
        this.canHuntWhenTamed = canHuntWhenTamed;
        this.huntRangeXZ = huntRangeXZ;
        this.huntRangeY = huntRangeY;
        this.flyingSpeed = flyingSpeed;
        this.groundSpeed = groundSpeed;
        this.attackCooldownTicks = attackCooldownTicks;
    }

    @Override
    protected boolean shouldRun(ServerWorld world, T bird) {
        if (!this.canHunt(bird)) {
            return false;
        }

        Optional<LivingEntity> target = this.findTarget(bird);
        if (target.isEmpty()) {
            return false;
        }

        bird.getBrain().remember(MemoryModuleType.ATTACK_TARGET, target.get());
        return true;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld world, T bird, long time) {
        Optional<LivingEntity> optionalTarget = bird.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
        if (optionalTarget.isEmpty()) {
            return false;
        }

        LivingEntity target = optionalTarget.get();
        return this.canHunt(bird) && this.isValidTarget(bird, target);
    }

    @Override
    protected void run(ServerWorld world, T bird, long time) {
        this.attackCooldown = 0;
        this.updateAttackTarget(bird);

        if (!bird.isFlying() && bird.canStartFlying()) {
            bird.startFlying();
        }
    }

    @Override
    protected void keepRunning(ServerWorld world, T bird, long time) {
        this.updateAttackTarget(bird);

        Optional<LivingEntity> optionalTarget = bird.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
        if (optionalTarget.isEmpty()) {
            return;
        }

        LivingEntity target = optionalTarget.get();
        double distanceSq = bird.squaredDistanceTo(target);

        bird.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));

        if (!bird.isFlying() && bird.canStartFlying()) {
            bird.startFlying();
        }

        if (bird.isFlying()) {
            Vec3d targetPos = target.getPos().add(0.0D, target.getHeight() * 0.5D, 0.0D);
            bird.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.flyingSpeed, 0));
        } else {
            bird.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityLookTarget(target, false), this.groundSpeed, 0));
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        double attackReach = bird.getWidth() + target.getWidth() + 0.6D;
        double attackReachSq = attackReach * attackReach;

        if (distanceSq <= attackReachSq) {
            bird.getNavigation().stop();

            if (this.attackCooldown <= 0) {
                bird.tryAttack(target);
                this.attackCooldown = this.attackCooldownTicks;
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld world, T bird, long time) {
        bird.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        bird.getBrain().forget(MemoryModuleType.WALK_TARGET);
        bird.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    private void updateAttackTarget(T bird) {
        Optional<LivingEntity> optionalTarget = bird.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
        if (optionalTarget.isPresent() && this.isValidTarget(bird, optionalTarget.get())) {
            return;
        }

        Optional<LivingEntity> newTarget = this.findTarget(bird);
        if (newTarget.isPresent()) {
            bird.getBrain().remember(MemoryModuleType.ATTACK_TARGET, newTarget.get());
        } else {
            bird.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        }
    }

    private Optional<LivingEntity> findTarget(T bird) {
        Box searchBox = bird.getBoundingBox().expand(this.huntRangeXZ, this.huntRangeY, this.huntRangeXZ);

        List<LivingEntity> targets = bird.getWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                target -> this.isValidTarget(bird, target)
        );

        return targets.stream().min(Comparator.comparingDouble(bird::squaredDistanceTo));
    }

    private boolean canHunt(T bird) {
        return (this.canHuntWhenTamed || !bird.isTamed())
                && !bird.isBaby()
                && !bird.isSitting()
                && !bird.hasVehicle();
    }

    private boolean isValidTarget(T bird, LivingEntity target) {
        return target.isAlive()
                && target != bird
                && target.getType().isIn(this.huntTag)
                && bird.canSee(target);
    }
}

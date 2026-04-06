package net.domixcze.domixscreatures.entity.ai.goal.entity;

import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.EnumSet;

public class CaveWyvernPoisonBreathGoal extends Goal {

    private final CaveWyvernEntity wyvern;
    private LivingEntity target;

    private int breathTicks;

    private static final int MAX_BREATH_TICKS = 60;
    private static final int COOLDOWN_TICKS = 200;
    private static final double RANGE = 8.0D;

    public CaveWyvernPoisonBreathGoal(CaveWyvernEntity wyvern) {
        this.wyvern = wyvern;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.wyvern.isPale() && this.wyvern.isExposedToSun()) return false;
        if (wyvern.getPoisonBreathCooldown() > 0) return false;
        if (wyvern.isBaby() || wyvern.isSleeping()) return false;

        LivingEntity target = wyvern.getTarget();
        if (target == null || !target.isAlive()) return false;

        double distSq = wyvern.squaredDistanceTo(target);
        return distSq >= 9.0 && distSq <= (RANGE * RANGE);
    }

    @Override
    public void start() {
        this.target = wyvern.getTarget();
        this.breathTicks = 0;
        wyvern.getNavigation().stop();
    }

    @Override
    public boolean shouldContinue() {
        if (this.wyvern.isPale() && this.wyvern.isExposedToSun()) return false;

        double maxReachSq = (RANGE + 1.0) * (RANGE + 1.0);

        return target != null
                && target.isAlive()
                && breathTicks < MAX_BREATH_TICKS
                && wyvern.squaredDistanceTo(target) <= maxReachSq
                && wyvern.getVisibilityCache().canSee(target);
    }

    @Override
    public void stop() {
        this.target = null;
        if (this.breathTicks >= MAX_BREATH_TICKS) {
            wyvern.setPoisonBreathCooldown(COOLDOWN_TICKS);

        } else if (this.breathTicks > 10) {
            wyvern.setPoisonBreathCooldown(100);

        } else {
            wyvern.setPoisonBreathCooldown(20);
        }

        this.breathTicks = 0;
        wyvern.getNavigation().recalculatePath();
        wyvern.setBreathingPoison(false);
    }

    @Override
    public void tick() {
        if (target == null) return;

        World world = wyvern.getWorld();
        wyvern.getNavigation().stop();
        wyvern.getLookControl().lookAt(target, 30.0F, 30.0F);

        double diffX = target.getX() - wyvern.getX();
        double diffY = target.getEyeY() - wyvern.getEyeY();
        double diffZ = target.getZ() - wyvern.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float targetPitch = (float)(-Math.atan2(diffY, diffXZ) * 57.2957763671875D);

        boolean canHitTarget = Math.abs(targetPitch) <= 45.0F && wyvern.getVisibilityCache().canSee(target);

        if (canHitTarget) {
            wyvern.setBreathingPoison(true);
            breathTicks++;

            Vec3d lookDir = Vec3d.fromPolar(wyvern.getPitch(), wyvern.headYaw);
            Vec3d yawForward = Vec3d.fromPolar(0.0F, wyvern.headYaw);
            Vec3d down = new Vec3d(0, -1, 0);

            Vec3d start = wyvern.getPos()
                    .add(0.0, wyvern.getEyeHeight(wyvern.getPose()), 0.0)
                    .add(yawForward.multiply(2.0))
                    .add(down.multiply(0.8));

            Vec3d end = start.add(lookDir.multiply(RANGE));

            BlockHitResult blockHit = world.raycast(new RaycastContext(
                    start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, wyvern
            ));

            Vec3d hitPos = blockHit.getPos();

            EntityHitResult entityHit = ProjectileUtil.raycast(
                    wyvern, start, hitPos,
                    wyvern.getBoundingBox().stretch(lookDir.multiply(RANGE)).expand(1.0),
                    entity -> entity instanceof LivingEntity && entity != wyvern,
                    RANGE * RANGE
            );

            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity living) {
                boolean isBlocked = false;

                if (living.isBlocking()) {
                    Vec3d attackVec = start.relativize(living.getPos()).normalize();
                    Vec3d lookVec = living.getRotationVec(1.0F);
                    if (attackVec.dotProduct(lookVec) < 0.0) {
                        isBlocked = true;
                    }
                }

                if (breathTicks % 10 == 0) {
                    if (isBlocked) {
                        ItemStack shield = living.getActiveItem();
                        if (shield.getItem() instanceof ShieldItem) {
                            if (living instanceof PlayerEntity player) {
                                shield.damage(2, player, living.getActiveHand() == net.minecraft.util.Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                            } else {
                                living.damageShield(2.0F);
                            }
                        }

                        world.playSound(null, living.getX(), living.getY(), living.getZ(),
                                SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
                    } else {
                        living.damage(wyvern.getDamageSources().mobAttack(wyvern), 2.0F);
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60, 1), wyvern);
                    }
                }

                if (isBlocked) {
                    hitPos = entityHit.getPos();
                }
            }

            if (world instanceof ServerWorld serverWorld) {
                for (int i = 0; i < 4; i++) {
                    double dx = hitPos.x - start.x;
                    double dy = hitPos.y - start.y;
                    double dz = hitPos.z - start.z;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double speed = 0.8;
                    double spread = 0.04;

                    double vx = (dx / dist) * speed + (world.random.nextGaussian() * spread);
                    double vy = (dy / dist) * speed + (world.random.nextGaussian() * spread);
                    double vz = (dz / dist) * speed + (world.random.nextGaussian() * spread);

                    serverWorld.spawnParticles(ModParticles.POISON_BREATH, start.x, start.y, start.z, 0, vx, vy, vz, 1.0);
                }
            }
        } else {
            wyvern.setBreathingPoison(false);
        }
    }
}
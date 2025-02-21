package net.domixcze.domixscreatures.entity.ai;

import net.domixcze.domixscreatures.block.ModBlocks;
import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.entity.custom.SpectralBatEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScreechAttackGoal extends Goal {

    private final SpectralBatEntity bat;
    private static final double ATTACK_RANGE = 10.0;
    private final float DAMAGE = 6.0f;
    private static final double GLASS_CRACK_RADIUS = 8.0;
    private static final double GLASS_CRACK_CHANCE = 0.3;

    public ScreechAttackGoal(SpectralBatEntity bat) {
        this.bat = bat;
    }

    @Override
    public boolean canStart() {
        if (this.bat.isHanging()) {
            return false;
        }
        if (this.bat.isBaby()) {
            return false;
        }

        LivingEntity target = this.bat.getTarget();
        return target != null && this.bat.getScreechCooldown() == 0 && this.bat.squaredDistanceTo(target) <= ATTACK_RANGE * ATTACK_RANGE;
    }

    @Override
    public void start() {
        this.bat.setScreeching(true);
        this.bat.resetScreechCooldown();
        this.bat.resetScreechDuration();

        List<PlayerEntity> players = this.bat.getWorld().getEntitiesByClass(PlayerEntity.class, this.bat.getBoundingBox().expand(ATTACK_RANGE), player -> true);
        for (PlayerEntity player : players) {
            float volume = isProtectedFromScreech(player) ? 0.05f : 1f; // Lower volume for protected players
            player.playSound(ModSounds.SPECTRAL_BAT_SCREECH, SoundCategory.HOSTILE, volume, 1f);
        }

        this.spawnScreech();
        this.applyEffect();
        this.crackGlass();
    }

    @Override
    public void stop() {
        this.bat.setScreeching(false);
    }

    @Override
    public boolean shouldContinue() {
        return this.bat.getScreechDuration() > 0;
    }

    private void applyEffect() {
        List<PlayerEntity> players = this.bat.getWorld().getEntitiesByClass(PlayerEntity.class, this.bat.getBoundingBox().expand(ATTACK_RANGE), player -> true);
        for (PlayerEntity player : players) {
            if (isProtectedFromScreech(player)) {
                continue; // Skip protected players
            }
            player.addStatusEffect(new StatusEffectInstance(ModEffects.DEAFEN, 250, 0));
        }
    }

    private boolean isProtectedFromScreech(PlayerEntity player) {
        return player.getInventory().armor.get(3) != null
                && player.getInventory().armor.get(3).isOf(ModItems.SONIC_BLOCKERS);
    }


    private void crackGlass() {
        World world = this.bat.getWorld();
        BlockPos origin = new BlockPos((int) this.bat.getX(), (int) this.bat.getY(), (int) this.bat.getZ());
        int radius = (int) GLASS_CRACK_RADIUS;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (world.getBlockState(pos).isOf(Blocks.GLASS)) {
                        if (this.bat.getRandom().nextDouble() < GLASS_CRACK_CHANCE) {
                            world.setBlockState(pos, ModBlocks.CRACKED_GLASS_BLOCK.getDefaultState());
                            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK,
                                    SoundCategory.BLOCKS,
                                    1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }

    private void spawnScreech() {
        World world = this.bat.getWorld();
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            LivingEntity target = this.bat.getTarget();
            if (target != null) {

                Vec3d targetPos = target.getPos();
                Vec3d source = bat.getPos().add(0.0, 1.6f, 0.0);
                Vec3d offsetToTarget = targetPos.subtract(source);
                Vec3d normalized = offsetToTarget.normalize();

                Set<Entity> hitEntities = new HashSet<>();
                int particleCount = (int) Math.floor(offsetToTarget.length()) + 7;

                for (int i = 1; i <= particleCount; ++i) {
                    Vec3d particlePos = source.add(normalized.multiply(i));
                    serverWorld.spawnParticles(ModParticles.SCREECH, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);

                    BlockPos blockPos = new BlockPos((int) particlePos.x, (int) particlePos.y, (int) particlePos.z);
                    Box hitBox = new Box(blockPos).expand(2);
                    List<LivingEntity> entitiesHit = world.getEntitiesByClass(LivingEntity.class, hitBox, entity -> entity != bat);

                    hitEntities.addAll(entitiesHit);

                    for (Entity hitEntity : hitEntities) {
                        if (hitEntity instanceof PlayerEntity player && isProtectedFromScreech(player)) {
                            continue; // Skip protected players
                        }
                        if (hitEntity instanceof LivingEntity livingEntity) {
                            livingEntity.damage(bat.getDamageSources().mobAttack/*.sonicBoom*/(bat), DAMAGE);
                        }
                    }
                }
            }
        }
    }
}
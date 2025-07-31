package net.domixcze.domixscreatures.effect;

import net.domixcze.domixscreatures.damage.ModDamageTypes;
import net.domixcze.domixscreatures.particle.ModParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

public class BleedingEffect extends StatusEffect {
    protected BleedingEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient()) {
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.BLEEDING), 1.0F);

            ServerWorld world = (ServerWorld) entity.getWorld();
            for (int i = 0; i < 3; i++) {
                double spawnX = entity.getX() + (world.random.nextDouble() - 0.5) * entity.getWidth() * 0.7;
                double spawnY = entity.getY() + 0.1 + (world.random.nextDouble() * (entity.getHeight() * 0.7));
                double spawnZ = entity.getZ() + (world.random.nextDouble() - 0.5) * entity.getWidth() * 0.7;

                world.spawnParticles(
                        ModParticles.FALLING_BLOOD,
                        spawnX, spawnY, spawnZ,
                        1,
                        0.0, 0.0, 0.0,
                        0.0
                );
            }
        }

        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override //disables the vanilla particles
    public ParticleEffect createParticle(StatusEffectInstance effect) {
        return EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, 0);
    }
}
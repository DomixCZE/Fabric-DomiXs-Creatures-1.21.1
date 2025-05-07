package net.domixcze.domixscreatures.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EcholocationEffect extends StatusEffect {

    protected EcholocationEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            World world = entity.getEntityWorld();
            BlockPos playerPos = entity.getBlockPos();

            int radius = 25;

            Vec3d min = Vec3d.of(playerPos).add(-radius, -radius, -radius);
            Vec3d max = Vec3d.of(playerPos).add(radius, radius, radius);

            List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, new Box(min, max), e -> e != entity);

            for (LivingEntity nearbyEntity : nearbyEntities) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
            }
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

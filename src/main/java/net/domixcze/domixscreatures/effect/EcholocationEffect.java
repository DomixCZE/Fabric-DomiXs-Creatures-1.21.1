package net.domixcze.domixscreatures.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EcholocationEffect extends StatusEffect {

    protected EcholocationEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity) {
            World world = entity.getEntityWorld();
            BlockPos playerPos = entity.getBlockPos();

            int radius = 25;

            List<LivingEntity> nearbyEntities = world.getEntitiesByClass(LivingEntity.class, new Box(
                    playerPos.add(-radius, -radius, -radius),
                    playerPos.add(radius, radius, radius)
            ), e -> e != entity);

            for (LivingEntity nearbyEntity : nearbyEntities) {
                nearbyEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0));
            }
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

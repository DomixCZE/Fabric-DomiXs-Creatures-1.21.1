package net.domixcze.domixscreatures.effect;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.damage.ModDamageTypes;
import net.domixcze.domixscreatures.entity.ModEntities;
import net.domixcze.domixscreatures.sound.ModSounds;
import net.domixcze.domixscreatures.util.ModTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ElectrifiedEffect extends StatusEffect {

    public static final Identifier MOVEMENT_SPEED_MODIFIER_ID = Identifier.of(DomiXsCreatures.MOD_ID, "electrified_speed_debuff");

    public ElectrifiedEffect(StatusEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                MOVEMENT_SPEED_MODIFIER_ID,
                -0.25,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity.getWorld().getTime() % 20 != 0) {
            return true;
        }

        if (entity.getType() == ModEntities.EEL) {
            return true;
        }

        if (entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) {
            return true;
        }

        int conductiveItemCount = 0;

        for (ItemStack armor : entity.getArmorItems()) {
            if (armor.isIn(ModTags.Items.CONDUCTIVE_ITEMS)) {
                conductiveItemCount++;
            }
        }

        if (entity.getMainHandStack().isIn(ModTags.Items.CONDUCTIVE_ITEMS)) {
            conductiveItemCount++;
        }
        if (entity.getOffHandStack().isIn(ModTags.Items.CONDUCTIVE_ITEMS)) {
            conductiveItemCount++;
        }

        boolean inWater = entity.isTouchingWater();
        float damage = 0.0f;

        if (inWater) {
            damage = (conductiveItemCount > 0) ? 8.0f : 4.0f;
        } else if (conductiveItemCount > 0) {
            damage = 4.0f;
        }

        if (damage > 0.0f && !entity.getWorld().isClient) {
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.ELECTRIC), damage);

            if (entity.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.playSound(
                        null,
                        entity.getX(), entity.getY(), entity.getZ(),
                        ModSounds.ZAP,
                        SoundCategory.AMBIENT,
                        1.0f, // Volume
                        1.0f  // Pitch
                );
            }
        }
        return true;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
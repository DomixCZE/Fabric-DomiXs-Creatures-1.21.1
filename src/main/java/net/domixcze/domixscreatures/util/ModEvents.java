package net.domixcze.domixscreatures.util;

import net.domixcze.domixscreatures.effect.ModEffects;
import net.domixcze.domixscreatures.item.ModItems;
import net.domixcze.domixscreatures.item.custom.MacuahuitlItem;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ModEvents {

    public static void registerModEvents() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(ModEvents::onLivingEntityDamage);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                ItemStack maskStack = hasPoisonImmunityItem(player);
                if (!maskStack.isEmpty() && player.hasStatusEffect(StatusEffects.POISON)) {
                    player.removeStatusEffect(StatusEffects.POISON);

                    if (!player.getWorld().isClient) {
                        maskStack.damage(1, player, EquipmentSlot.HEAD);
                    }
                }
            }
        });
    }

    private static boolean onLivingEntityDamage(LivingEntity entity, DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity attacker) {
            ItemStack heldStack = attacker.getMainHandStack();

            if (heldStack.getItem() instanceof MacuahuitlItem
                    && !attacker.getWorld().isClient()
                    && !(attacker instanceof PlayerEntity)) {

                heldStack.damage(1, attacker, EquipmentSlot.MAINHAND);

                if (attacker.getWorld().random.nextFloat() < 0.15f) { // 15% chance
                    entity.addStatusEffect(new StatusEffectInstance(ModEffects.BLEEDING, 100, 0));
                }
            }
        }

        if (source.getSource() != null && source.getSource().getType().isIn(ModTags.EntityTypes.REFLECTABLE_PROJECTILES)) {
            int reflectiveArmorPieces = 0;
            for (ItemStack armorStack : entity.getArmorItems()) {
                if (armorStack.isIn(ModTags.Items.REFLECTIVE_ARMOR)) {
                    reflectiveArmorPieces++;
                }
            }

            // Determine chance
            double reflectionChance = switch (reflectiveArmorPieces) {
                case 1 -> 0.10;
                case 2 -> 0.20;
                case 3 -> 0.40;
                case 4 -> 0.60;
                default -> 0.0;
            };

            if (reflectionChance > 0 && entity.getRandom().nextFloat() < reflectionChance) {
                entity.getWorld().playSound(
                        null,
                        entity.getBlockPos(),
                        SoundEvents.ITEM_SHIELD_BLOCK,
                        SoundCategory.PLAYERS,
                        1.0F,
                        1.0F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F
                );

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                        ItemStack armorStack = entity.getEquippedStack(slot);
                        if (armorStack.isIn(ModTags.Items.REFLECTIVE_ARMOR)) {
                            armorStack.damage(1, entity, slot);
                        }
                    }
                }

                return false;
            }
        }

        return true;
    }

    private static ItemStack hasPoisonImmunityItem(LivingEntity entity) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.HEAD);
        if (stack.isOf(ModItems.SHAMAN_MASK)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }
}
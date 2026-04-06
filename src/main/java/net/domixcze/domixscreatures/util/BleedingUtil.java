package net.domixcze.domixscreatures.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public final class BleedingUtil {
    private BleedingUtil() {
    }

    public static boolean hasFullBleedingProtection(LivingEntity entity) {
        return getBleedingProtectionCount(entity) == 4;
    }

    public static boolean isImmuneToBleeding(Entity entity) {
        return entity.getType().isIn(ModTags.EntityTypes.IMMUNE_TO_BLEEDING);
    }

    public static boolean hasReducedBleeding(Entity entity) {
        return entity.getType().isIn(ModTags.EntityTypes.REDUCED_BLEEDING);
    }

    //Checks if the given item stack is tagged as preventing bleeding.
    public static boolean preventsBleeding(ItemStack stack) {
        return stack.isIn(ModTags.Items.PREVENTS_BLEEDING);
    }

     //Counts how many armor pieces are tagged to prevent bleeding.
    public static int getBleedingProtectionCount(LivingEntity entity) {
        int count = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack stack = entity.getEquippedStack(slot);
                if (preventsBleeding(stack)) {
                    count++;
                }
            }
        }
        return count;
    }

    public static int getReducedDuration(LivingEntity entity, int incomingDuration) {
        if (isImmuneToBleeding(entity) || hasFullBleedingProtection(entity)) {
            return 0;
        }

        int protectionCount = getBleedingProtectionCount(entity);
        double multiplier = 1.0;

        if (protectionCount == 3) {
            multiplier = 0.42;
        }
        else if (protectionCount >= 1) {
            multiplier = 0.72;
        }

        int finalDuration = (int) Math.round(incomingDuration * multiplier);

        if (hasReducedBleeding(entity)) {
            finalDuration /= 2;
        }

        return finalDuration;
    }
}
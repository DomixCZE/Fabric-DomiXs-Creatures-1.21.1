package net.domixcze.domixscreatures.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class BleedingUtil {
    public static boolean hasFullBleedingProtection(LivingEntity entity) {
        return getBleedingProtectionCount(entity) == 4;
    }

    public static boolean isImmuneToBleeding(Entity entity) {
        return entity.getType().isIn(ModTags.EntityTypes.IMMUNE_TO_BLEEDING);
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

     //Checks if the given item stack is tagged as preventing bleeding.
    public static boolean preventsBleeding(ItemStack stack) {
        return stack.isIn(ModTags.Items.PREVENTS_BLEEDING);
    }
}
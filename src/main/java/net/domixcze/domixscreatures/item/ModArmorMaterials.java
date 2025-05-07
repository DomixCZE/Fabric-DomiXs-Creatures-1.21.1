package net.domixcze.domixscreatures.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.List;
import java.util.Map;

public class ModArmorMaterials {
    public static final ArmorMaterial COSMETIC = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 0,
                    ArmorItem.Type.LEGGINGS, 0,
                    ArmorItem.Type.CHESTPLATE, 0,
                    ArmorItem.Type.HELMET, 0
            ),
            0,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(Items.LEATHER),
            List.of(),
            0.0F,
            0.0F
    );

    public static final ArmorMaterial UTILITY = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 0,
                    ArmorItem.Type.LEGGINGS, 0,
                    ArmorItem.Type.CHESTPLATE, 0,
                    ArmorItem.Type.HELMET, 0
            ),
            0,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(Items.SHULKER_SHELL),
            List.of(),
            0.0F,
            0.0F
    );

    public static final ArmorMaterial SALAMANDER = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 0,
                    ArmorItem.Type.LEGGINGS, 0,
                    ArmorItem.Type.CHESTPLATE, 0,
                    ArmorItem.Type.HELMET, 0
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
            () -> Ingredient.ofItems(ModItems.FIRE_SALAMANDER_SCALES),
            List.of(),
            3.0F,
            0.2F
    );

    public static final ArmorMaterial CROCODILE = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 3,
                    ArmorItem.Type.LEGGINGS, 6,
                    ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.HELMET, 3
            ),
            15,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(ModItems.CROCODILE_SCALE),
            List.of(),
            2.5F,
            0.0F
    );

    public static final ArmorMaterial CROCODILE_ALBINO = new ArmorMaterial(
            Map.of(
                    ArmorItem.Type.BOOTS, 3,
                    ArmorItem.Type.LEGGINGS, 6,
                    ArmorItem.Type.CHESTPLATE, 8,
                    ArmorItem.Type.HELMET, 3
            ),
            18,
            SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            () -> Ingredient.ofItems(ModItems.CROCODILE_SCALE_ALBINO),
            List.of(),
            2.5F,
            0.0F
    );

    public static int getDurability(ArmorMaterial material, ArmorItem.Type type) {
        int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
        int multiplier = 1;

        if (material == UTILITY || material == SALAMANDER) {
            multiplier = 40;
        } else if (material == CROCODILE || material == CROCODILE_ALBINO) {
            multiplier = 25;
        }

        return BASE_DURABILITY[type.ordinal()] * multiplier;
    }

    public static int getProtection(ArmorMaterial material, ArmorItem.Type type) {
        return material.defense().getOrDefault(type, 0);
    }

    public static int getEnchantability(ArmorMaterial material) {
        return material.enchantability();
    }

    public static RegistryEntry<SoundEvent> getEquipSound(ArmorMaterial material) {
        return material.equipSound();
    }

    public static Ingredient getRepairIngredient(ArmorMaterial material) {
        return material.repairIngredient().get();
    }

    public static float getToughness(ArmorMaterial material) {
        return material.toughness();
    }

    public static float getKnockbackResistance(ArmorMaterial material) {
        return material.knockbackResistance();
    }
}
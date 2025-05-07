package net.domixcze.domixscreatures.item.custom;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;

public class SalamanderUpgradeSmithingTemplateItem extends SmithingTemplateItem {

    public static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    public static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;

    public static final Text APPLIES_TO_TEXT = Text.translatable("item.domixs-creatures.salamander_upgrade_template.applies_to").formatted(DESCRIPTION_FORMATTING);
    public static final Text INGREDIENTS_TEXT = Text.translatable("item.domixs-creatures.salamander_upgrade_template.ingredients").formatted(DESCRIPTION_FORMATTING);
    public static final Text BASE_SLOT_DESCRIPTION_TEXT = Text.translatable("item.domixs-creatures.salamander_upgrade_template.base_slot_description");
    public static final Text ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable("item.domixs-creatures.salamander_upgrade_template.additions_slot_description");

    private static final Identifier EMPTY_ARMOR_SLOT_HELMET_TEXTURE = Identifier.of("item/empty_armor_slot_helmet");
    private static final Identifier EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE = Identifier.of("item/empty_armor_slot_chestplate");
    private static final Identifier EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE = Identifier.of("item/empty_armor_slot_leggings");
    private static final Identifier EMPTY_ARMOR_SLOT_BOOTS_TEXTURE = Identifier.of("item/empty_armor_slot_boots");
    private static final Identifier EMPTY_SLOT_INGOT_TEXTURE = Identifier.of("domixs-creatures:item/empty_fire_salamander_scales");

    public SalamanderUpgradeSmithingTemplateItem(Settings settings) {
        super(APPLIES_TO_TEXT, INGREDIENTS_TEXT, Text.translatable(Util.createTranslationKey("upgrade", Identifier.of(DomiXsCreatures.MOD_ID, "salamander_upgrade"))).formatted(TITLE_FORMATTING), BASE_SLOT_DESCRIPTION_TEXT, ADDITIONS_SLOT_DESCRIPTION_TEXT, List.of(EMPTY_ARMOR_SLOT_HELMET_TEXTURE, EMPTY_ARMOR_SLOT_CHESTPLATE_TEXTURE, EMPTY_ARMOR_SLOT_LEGGINGS_TEXTURE, EMPTY_ARMOR_SLOT_BOOTS_TEXTURE), List.of(EMPTY_SLOT_INGOT_TEXTURE));
    }
}
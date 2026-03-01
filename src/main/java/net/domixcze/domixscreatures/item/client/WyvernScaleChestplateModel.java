package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.WyvernScaleChestplateItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WyvernScaleChestplateModel extends GeoModel<WyvernScaleChestplateItem> {

    @Override
    public Identifier getModelResource(WyvernScaleChestplateItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/wyvern_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WyvernScaleChestplateItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/wyvern_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(WyvernScaleChestplateItem animatable) {
        return null;
    }
}
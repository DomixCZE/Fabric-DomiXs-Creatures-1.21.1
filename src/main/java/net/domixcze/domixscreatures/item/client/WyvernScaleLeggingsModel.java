package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.WyvernScaleLeggingsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WyvernScaleLeggingsModel extends GeoModel<WyvernScaleLeggingsItem> {

    @Override
    public Identifier getModelResource(WyvernScaleLeggingsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/wyvern_scale_leggings.geo.json");
    }

    @Override
    public Identifier getTextureResource(WyvernScaleLeggingsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/wyvern_scale_leggings.png");
    }

    @Override
    public Identifier getAnimationResource(WyvernScaleLeggingsItem animatable) {
        return null;
    }
}
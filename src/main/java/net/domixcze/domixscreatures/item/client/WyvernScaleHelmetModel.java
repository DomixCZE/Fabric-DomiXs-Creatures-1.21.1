package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.WyvernScaleHelmetItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WyvernScaleHelmetModel extends GeoModel<WyvernScaleHelmetItem> {

    @Override
    public Identifier getModelResource(WyvernScaleHelmetItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/wyvern_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WyvernScaleHelmetItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/wyvern_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(WyvernScaleHelmetItem animatable) {
        return null;
    }
}
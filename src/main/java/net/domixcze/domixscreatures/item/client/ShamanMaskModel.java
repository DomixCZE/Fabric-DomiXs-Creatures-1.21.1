package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ShamanMaskItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ShamanMaskModel extends GeoModel<ShamanMaskItem> {

    @Override
    public Identifier getModelResource(ShamanMaskItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"geo/shaman_mask.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShamanMaskItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"textures/armor/shaman_mask.png");
    }

    @Override
    public Identifier getAnimationResource(ShamanMaskItem animatable) {
        return null;
    }
}
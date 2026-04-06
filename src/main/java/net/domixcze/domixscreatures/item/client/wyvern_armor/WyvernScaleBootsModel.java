package net.domixcze.domixscreatures.item.client.wyvern_armor;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.armor.WyvernScaleBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WyvernScaleBootsModel extends GeoModel<WyvernScaleBootsItem> {

    @Override
    public Identifier getModelResource(WyvernScaleBootsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/wyvern_scale_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(WyvernScaleBootsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/wyvern_scale_armor.png");
    }

    @Override
    public Identifier getAnimationResource(WyvernScaleBootsItem animatable) {
        return null;
    }
}
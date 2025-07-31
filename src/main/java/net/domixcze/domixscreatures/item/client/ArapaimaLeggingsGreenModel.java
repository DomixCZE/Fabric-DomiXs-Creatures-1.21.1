package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaLeggingsGreenItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaLeggingsGreenModel extends GeoModel<ArapaimaLeggingsGreenItem> {

    @Override
    public Identifier getModelResource(ArapaimaLeggingsGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_leggings.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaLeggingsGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_leggings_green.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaLeggingsGreenItem animatable) {
        return null;
    }
}
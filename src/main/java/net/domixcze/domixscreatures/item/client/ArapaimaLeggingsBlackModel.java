package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaLeggingsBlackItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaLeggingsBlackModel extends GeoModel<ArapaimaLeggingsBlackItem> {

    @Override
    public Identifier getModelResource(ArapaimaLeggingsBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_leggings.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaLeggingsBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_leggings_black.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaLeggingsBlackItem animatable) {
        return null;
    }
}
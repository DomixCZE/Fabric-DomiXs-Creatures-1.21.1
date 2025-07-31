package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaHelmetGreenItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaHelmetGreenModel extends GeoModel<ArapaimaHelmetGreenItem> {

    @Override
    public Identifier getModelResource(ArapaimaHelmetGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaHelmetGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_green.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaHelmetGreenItem animatable) {
        return null;
    }
}
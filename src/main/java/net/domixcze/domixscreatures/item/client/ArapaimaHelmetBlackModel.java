package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaHelmetBlackItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaHelmetBlackModel extends GeoModel<ArapaimaHelmetBlackItem> {

    @Override
    public Identifier getModelResource(ArapaimaHelmetBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaHelmetBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_black.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaHelmetBlackItem animatable) {
        return null;
    }
}
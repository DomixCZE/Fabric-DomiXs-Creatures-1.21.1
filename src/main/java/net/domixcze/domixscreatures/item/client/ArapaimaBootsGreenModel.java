package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaBootsGreenItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaBootsGreenModel extends GeoModel<ArapaimaBootsGreenItem> {

    @Override
    public Identifier getModelResource(ArapaimaBootsGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaBootsGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_green.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaBootsGreenItem animatable) {
        return null;
    }
}
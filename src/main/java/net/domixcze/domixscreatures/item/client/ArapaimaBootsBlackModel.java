package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaBootsBlackItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaBootsBlackModel extends GeoModel<ArapaimaBootsBlackItem> {

    @Override
    public Identifier getModelResource(ArapaimaBootsBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaBootsBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_black.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaBootsBlackItem animatable) {
        return null;
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaChestplateGreenItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaChestplateGreenModel extends GeoModel<ArapaimaChestplateGreenItem> {

    @Override
    public Identifier getModelResource(ArapaimaChestplateGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaChestplateGreenItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_green.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaChestplateGreenItem animatable) {
        return null;
    }
}
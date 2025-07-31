package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.ArapaimaChestplateBlackItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaChestplateBlackModel extends GeoModel<ArapaimaChestplateBlackItem> {

    @Override
    public Identifier getModelResource(ArapaimaChestplateBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/arapaima_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaChestplateBlackItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/arapaima_armor_black.png");
    }

    @Override
    public Identifier getAnimationResource(ArapaimaChestplateBlackItem animatable) {
        return null;
    }
}
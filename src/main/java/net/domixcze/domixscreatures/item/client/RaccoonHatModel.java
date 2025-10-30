package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.RaccoonHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RaccoonHatModel extends GeoModel<RaccoonHatItem> {

    @Override
    public Identifier getModelResource(RaccoonHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/raccoon_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(RaccoonHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/raccoon_hat.png");
    }

    @Override
    public Identifier getAnimationResource(RaccoonHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"animations/raccoon_hat.animation.json");
    }
}
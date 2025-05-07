package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.MediumAntlerHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MediumAntlerHatModel extends GeoModel<MediumAntlerHatItem> {

    @Override
    public Identifier getModelResource(MediumAntlerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/antler_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(MediumAntlerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/medium_antler_hat.png");
    }

    @Override
    public Identifier getAnimationResource(MediumAntlerHatItem animatable) {
        return null;
    }
}
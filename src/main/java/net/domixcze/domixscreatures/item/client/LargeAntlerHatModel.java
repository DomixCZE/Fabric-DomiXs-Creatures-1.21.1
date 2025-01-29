package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.LargeAntlerHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class LargeAntlerHatModel extends GeoModel<LargeAntlerHatItem> {

    @Override
    public Identifier getModelResource(LargeAntlerHatItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"geo/antler_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(LargeAntlerHatItem animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID,"textures/armor/large_antler_hat.png");
    }

    @Override
    public Identifier getAnimationResource(LargeAntlerHatItem animatable) {
        return null;
    }
}
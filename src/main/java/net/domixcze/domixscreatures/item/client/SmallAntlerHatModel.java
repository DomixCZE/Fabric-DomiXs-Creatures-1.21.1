package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SmallAntlerHatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SmallAntlerHatModel extends GeoModel<SmallAntlerHatItem> {

    @Override
    public Identifier getModelResource(SmallAntlerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/antler_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(SmallAntlerHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/small_antler_hat.png");
    }

    @Override
    public Identifier getAnimationResource(SmallAntlerHatItem animatable) {
        return null;
    }
}
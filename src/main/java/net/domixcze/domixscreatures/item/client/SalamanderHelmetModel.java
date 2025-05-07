package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SalamanderHelmetItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SalamanderHelmetModel extends GeoModel<SalamanderHelmetItem> {

    @Override
    public Identifier getModelResource(SalamanderHelmetItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/salamander_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(SalamanderHelmetItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/salamander_armor.png");
    }

    @Override
    public Identifier getAnimationResource(SalamanderHelmetItem animatable) {
        return null;
    }
}
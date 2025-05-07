package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SalamanderBootsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SalamanderBootsModel extends GeoModel<SalamanderBootsItem> {

    @Override
    public Identifier getModelResource(SalamanderBootsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/salamander_armor.geo.json");
    }

    @Override
    public Identifier getTextureResource(SalamanderBootsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/salamander_armor.png");
    }

    @Override
    public Identifier getAnimationResource(SalamanderBootsItem animatable) {
        return null;
    }
}
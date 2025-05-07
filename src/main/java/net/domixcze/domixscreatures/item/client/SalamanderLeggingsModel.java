package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SalamanderLeggingsItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SalamanderLeggingsModel extends GeoModel<SalamanderLeggingsItem> {

    @Override
    public Identifier getModelResource(SalamanderLeggingsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/salamander_leggings.geo.json");
    }

    @Override
    public Identifier getTextureResource(SalamanderLeggingsItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/salamander_leggings.png");
    }

    @Override
    public Identifier getAnimationResource(SalamanderLeggingsItem animatable) {
        return null;
    }
}
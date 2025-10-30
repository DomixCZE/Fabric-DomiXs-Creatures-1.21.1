package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.JadeCrownItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class JadeCrownModel extends GeoModel<JadeCrownItem> {

    @Override
    public Identifier getModelResource(JadeCrownItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/jade_crown.geo.json");
    }

    @Override
    public Identifier getTextureResource(JadeCrownItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/jade_crown.png");
    }

    @Override
    public Identifier getAnimationResource(JadeCrownItem animatable) {
        return null;
    }
}
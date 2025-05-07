package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.SonicBlockersItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SonicBlockersModel extends GeoModel<SonicBlockersItem> {

    @Override
    public Identifier getModelResource(SonicBlockersItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/sonic_blockers.geo.json");
    }

    @Override
    public Identifier getTextureResource(SonicBlockersItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/sonic_blockers.png");
    }

    @Override
    public Identifier getAnimationResource(SonicBlockersItem animatable) {
        return null;
    }
}
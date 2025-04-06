package net.domixcze.domixscreatures.entity.client.shaman;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.ShamanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ShamanModel extends GeoModel<ShamanEntity> {
    @Override
    public Identifier getModelResource(ShamanEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "geo/shaman.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShamanEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/shaman.png");
    }

    @Override
    public Identifier getAnimationResource(ShamanEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "animations/shaman.animation.json");
    }
}
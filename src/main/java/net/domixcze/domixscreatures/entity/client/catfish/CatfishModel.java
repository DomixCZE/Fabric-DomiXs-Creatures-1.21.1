package net.domixcze.domixscreatures.entity.client.catfish;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CatfishEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CatfishModel extends GeoModel<CatfishEntity> {
    @Override
    public Identifier getModelResource(CatfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/catfish.geo.json");
    }

    @Override
    public Identifier getTextureResource(CatfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/catfish.png");
    }

    @Override
    public Identifier getAnimationResource(CatfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/catfish.animation.json");
    }
}
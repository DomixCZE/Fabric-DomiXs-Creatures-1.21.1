package net.domixcze.domixscreatures.entity.client.butterfly;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.ButterflyEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ButterflyModel extends GeoModel<ButterflyEntity> {
    @Override
    public Identifier getModelResource(ButterflyEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/butterfly.geo.json");
    }

    @Override
    public Identifier getTextureResource(ButterflyEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/butterfly.png");
    }

    @Override
    public Identifier getAnimationResource(ButterflyEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/butterfly.animation.json");
    }
}
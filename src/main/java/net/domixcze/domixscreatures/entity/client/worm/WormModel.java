package net.domixcze.domixscreatures.entity.client.worm;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WormEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WormModel extends GeoModel<WormEntity> {
    @Override
    public Identifier getModelResource(WormEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/worm.geo.json");
    }

    @Override
    public Identifier getTextureResource(WormEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/worm.png");
    }

    @Override
    public Identifier getAnimationResource(WormEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/worm.animation.json");
    }
}
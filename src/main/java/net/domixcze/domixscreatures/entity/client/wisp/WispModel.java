package net.domixcze.domixscreatures.entity.client.wisp;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WispEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WispModel extends GeoModel<WispEntity> {
    @Override
    public Identifier getModelResource(WispEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "geo/wisp.geo.json");
    }

    @Override
    public Identifier getTextureResource(WispEntity animatable) {
        return animatable.isWearingSkull()
                ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/wisp.png")
                : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/wisp_without_skull.png");
    }

    @Override
    public Identifier getAnimationResource(WispEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "animations/wisp.animation.json");
    }
}
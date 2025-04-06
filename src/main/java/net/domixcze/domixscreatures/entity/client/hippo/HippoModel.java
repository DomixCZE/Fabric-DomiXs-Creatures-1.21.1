package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HippoModel extends GeoModel<HippoEntity> {
    @Override
    public Identifier getModelResource(HippoEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "geo/hippo.geo.json");
    }

    @Override
    public Identifier getTextureResource(HippoEntity animatable) {
        return animatable.getVariant() == HippoVariants.ALBINO
                ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/hippo_albino.png")
                : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/hippo.png");
    }

    @Override
    public Identifier getAnimationResource(HippoEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "animations/hippo.animation.json");
    }
}
package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HippoModel extends GeoModel<HippoEntity> {
    @Override
    public Identifier getModelResource(HippoEntity animatable) {

        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_hippo.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/hippo.geo.json");
    }

    @Override
    public Identifier getTextureResource(HippoEntity animatable) {
        if (animatable.getVariant() == HippoVariants.ALBINO) {
            return animatable.isBaby()
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hippo_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hippo_albino.png");
        }

        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hippo.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hippo.png");
    }

    @Override
    public Identifier getAnimationResource(HippoEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_hippo.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/hippo.animation.json");
    }
}
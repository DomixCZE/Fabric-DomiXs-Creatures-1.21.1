package net.domixcze.domixscreatures.entity.client.caterpillar;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CaterpillarEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CaterpillarModel extends GeoModel<CaterpillarEntity> {
    @Override
    public Identifier getModelResource(CaterpillarEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/caterpillar.geo.json");
    }

    @Override
    public Identifier getTextureResource(CaterpillarEntity animatable) {
        if (animatable.isCocoon()) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/cocoon.png");
        } else {
            return animatable.getVariant() == CaterpillarVariants.BROWN
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/caterpillar_brown.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/caterpillar_green.png");
        }
    }

    @Override
    public Identifier getAnimationResource(CaterpillarEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/caterpillar.animation.json");
    }
}
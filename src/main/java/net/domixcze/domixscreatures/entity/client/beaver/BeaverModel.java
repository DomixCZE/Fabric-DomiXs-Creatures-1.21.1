package net.domixcze.domixscreatures.entity.client.beaver;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BeaverEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BeaverModel extends GeoModel<BeaverEntity> {

    @Override
    public Identifier getModelResource(BeaverEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "geo/baby_beaver.geo.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "geo/beaver.geo.json");
    }

    @Override
    public Identifier getTextureResource(BeaverEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == BeaverVariants.ALBINO
                    ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_beaver_albino.png")
                    : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_beaver.png");
        } else {
            return animatable.getVariant() == BeaverVariants.ALBINO
                    ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/beaver_albino.png")
                    : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/beaver.png");
        }
    }

    @Override
    public Identifier getAnimationResource(BeaverEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "animations/baby_beaver.animation.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "animations/beaver.animation.json");
    }
}

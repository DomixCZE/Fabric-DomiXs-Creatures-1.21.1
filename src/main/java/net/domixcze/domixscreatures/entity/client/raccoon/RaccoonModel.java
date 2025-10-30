package net.domixcze.domixscreatures.entity.client.raccoon;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.RaccoonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RaccoonModel extends GeoModel<RaccoonEntity> {
    @Override
    public Identifier getModelResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_raccoon.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/raccoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_raccoon.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/raccoon.png");
    }

    @Override
    public Identifier getAnimationResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_raccoon.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/raccoon.animation.json");
    }
}
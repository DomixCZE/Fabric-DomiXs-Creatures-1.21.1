package net.domixcze.domixscreatures.entity.client.gorilla;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.GorillaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GorillaModel extends GeoModel<GorillaEntity> {
    @Override
    public Identifier getModelResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_gorilla.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/gorilla.geo.json");
    }

    @Override
    public Identifier getTextureResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_gorilla.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/gorilla.png");
    }

    @Override
    public Identifier getAnimationResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_gorilla.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/gorilla.animation.json");
    }
}
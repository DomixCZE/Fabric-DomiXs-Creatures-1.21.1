package net.domixcze.domixscreatures.entity.client.boar;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BoarEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BoarModel extends GeoModel<BoarEntity> {
    @Override
    public Identifier getModelResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_boar.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/boar.geo.json");
    }

    @Override
    public Identifier getTextureResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_boar.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/boar.png");
    }

    @Override
    public Identifier getAnimationResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_boar.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/boar.animation.json");
    }
}
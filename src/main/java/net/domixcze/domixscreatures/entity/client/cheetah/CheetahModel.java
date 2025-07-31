package net.domixcze.domixscreatures.entity.client.cheetah;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CheetahEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CheetahModel extends GeoModel<CheetahEntity> {
    @Override
    public Identifier getModelResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_cheetah.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/cheetah.geo.json");
    }

    @Override
    public Identifier getTextureResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_cheetah.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/cheetah.png");
    }

    @Override
    public Identifier getAnimationResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_cheetah.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/cheetah.animation.json");
    }
}
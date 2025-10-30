package net.domixcze.domixscreatures.entity.client.hyena;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HyenaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HyenaModel extends GeoModel<HyenaEntity> {
    @Override
    public Identifier getModelResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_hyena.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/hyena.geo.json");
    }

    @Override
    public Identifier getTextureResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hyena.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hyena.png");
    }

    @Override
    public Identifier getAnimationResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_hyena.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/hyena.animation.json");
    }
}
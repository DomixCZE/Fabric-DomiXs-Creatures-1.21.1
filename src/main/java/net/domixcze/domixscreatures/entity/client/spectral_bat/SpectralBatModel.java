package net.domixcze.domixscreatures.entity.client.spectral_bat;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.SpectralBatEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SpectralBatModel extends GeoModel<SpectralBatEntity> {
    @Override
    public Identifier getModelResource(SpectralBatEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_spectral_bat.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/spectral_bat.geo.json");
    }

    @Override
    public Identifier getTextureResource(SpectralBatEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_spectral_bat.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/spectral_bat.png");
    }

    @Override
    public Identifier getAnimationResource(SpectralBatEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_spectral_bat.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/spectral_bat.animation.json");
    }
}
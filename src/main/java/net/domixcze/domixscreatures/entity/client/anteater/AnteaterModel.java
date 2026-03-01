package net.domixcze.domixscreatures.entity.client.anteater;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AnteaterEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AnteaterModel extends GeoModel<AnteaterEntity> {
    @Override
    public Identifier getModelResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_anteater.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/anteater.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_anteater.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/anteater.png");
    }

    @Override
    public Identifier getAnimationResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_anteater.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/anteater.animation.json");
    }
}
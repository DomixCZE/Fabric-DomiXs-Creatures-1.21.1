package net.domixcze.domixscreatures.entity.client.eel;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.EelEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EelModel extends GeoModel<EelEntity> {
    @Override
    public Identifier getModelResource(EelEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/eel.geo.json");
    }

    @Override
    public Identifier getTextureResource(EelEntity animatable) {
        return switch (animatable.getVariant()) {
            case YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/eel_yellow.png");
            case ABYSS -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/eel_abyss.png");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/eel_green.png");
        };
    }

    @Override
    public Identifier getAnimationResource(EelEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/eel.animation.json");
    }
}
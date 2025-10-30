package net.domixcze.domixscreatures.entity.client.unicorn;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.UnicornEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class UnicornModel extends GeoModel<UnicornEntity> {
    @Override
    public Identifier getModelResource(UnicornEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/unicorn.geo.json");
    }

    @Override
    public Identifier getTextureResource(UnicornEntity animatable) {
        return switch (animatable.getVariant()) {
            case PINK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_pink.png");
            case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_blue.png");
            case YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_yellow.png");
            case GREEN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_green.png");
        };
    }

    @Override
    public Identifier getAnimationResource(UnicornEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/unicorn.animation.json");
    }
}
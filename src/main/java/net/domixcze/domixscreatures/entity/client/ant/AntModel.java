package net.domixcze.domixscreatures.entity.client.ant;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AntModel extends GeoModel<AntEntity> {
    @Override
    public Identifier getModelResource(AntEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/ant_queen.geo.json");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/ant_soldier.geo.json");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/ant_worker.geo.json");
        };
    }

    @Override
    public Identifier getTextureResource(AntEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/ant_queen.png");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/ant_soldier.png");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/ant_worker.png");
        };
    }

    @Override
    public Identifier getAnimationResource(AntEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/ant_queen.animation.json");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/ant_soldier.animation.json");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/ant_worker.animation.json");
        };
    }
}
package net.domixcze.domixscreatures.entity.client.termite;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.TermiteEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class TermiteModel extends GeoModel<TermiteEntity> {
    @Override
    public Identifier getModelResource(TermiteEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/termite_queen.geo.json");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/termite_soldier.geo.json");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/termite_worker.geo.json");
        };
    }

    @Override
    public Identifier getTextureResource(TermiteEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/termite_queen.png");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/termite_soldier.png");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/termite_worker.png");
        };
    }

    @Override
    public Identifier getAnimationResource(TermiteEntity animatable) {
        return switch (animatable.getVariant()) {
            case QUEEN -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/termite_queen.animation.json");
            case SOLDIER -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/termite_soldier.animation.json");
            default -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/termite_worker.animation.json");
        };
    }
}

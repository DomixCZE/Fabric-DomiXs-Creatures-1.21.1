package net.domixcze.domixscreatures.entity.client.hermit_crab;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HermitCrabEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HermitCrabModel extends GeoModel<HermitCrabEntity> {
    @Override
    public Identifier getModelResource(HermitCrabEntity animatable) {
        return switch (animatable.getShape()) {
            case ROUND -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/hermit_crab_round_shell.geo.json");
            case POINTY -> Identifier.of(DomiXsCreatures.MOD_ID, "geo/hermit_crab_pointy_shell.geo.json");
        };
    }

    @Override
    public Identifier getTextureResource(HermitCrabEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hermit_crab.png");
    }

    @Override
    public Identifier getAnimationResource(HermitCrabEntity animatable) {
        return switch (animatable.getShape()) {
            case ROUND -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/hermit_crab_round_shell.animation.json");
            case POINTY -> Identifier.of(DomiXsCreatures.MOD_ID, "animations/hermit_crab_pointy_shell.animation.json");
        };
    }
}
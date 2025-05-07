package net.domixcze.domixscreatures.entity.client.moose;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.MooseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class MooseModel extends GeoModel<MooseEntity> {
    @Override
    public Identifier getModelResource(MooseEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_moose.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/moose.geo.json");
    }

    @Override
    public Identifier getTextureResource(MooseEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == MooseVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_moose_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_moose.png");
        } else {
            return animatable.getVariant() == MooseVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/moose_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/moose.png");
        }
    }

    @Override
    public Identifier getAnimationResource(MooseEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_moose.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/moose.animation.json");
    }
}
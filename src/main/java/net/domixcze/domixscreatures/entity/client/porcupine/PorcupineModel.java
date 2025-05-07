package net.domixcze.domixscreatures.entity.client.porcupine;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PorcupineEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class PorcupineModel extends GeoModel<PorcupineEntity> {
    @Override
    public Identifier getModelResource(PorcupineEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_porcupine.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/porcupine.geo.json");
    }

    @Override
    public Identifier getTextureResource(PorcupineEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == PorcupineVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_porcupine_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_porcupine.png");
        } else {
            return animatable.getVariant() == PorcupineVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/porcupine_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/porcupine.png");
        }
    }

    @Override
    public Identifier getAnimationResource(PorcupineEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_porcupine.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/porcupine.animation.json");
    }
}
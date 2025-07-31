package net.domixcze.domixscreatures.entity.client.arapaima;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.ArapaimaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class ArapaimaModel extends GeoModel<ArapaimaEntity> {
    @Override
    public Identifier getModelResource(ArapaimaEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/arapaima.geo.json");
    }

    @Override
    public Identifier getTextureResource(ArapaimaEntity animatable) {
        return switch (animatable.getVariant()) {
            case GREEN_RED -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/arapaima_green_red.png");
            case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/arapaima_black.png");
            case BLACK_RED -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/arapaima_black_red.png");
        };
    }

    @Override
    public Identifier getAnimationResource(ArapaimaEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/arapaima.animation.json");
    }

    @Override
    public void setCustomAnimations(ArapaimaEntity entity, long instanceId, AnimationState<ArapaimaEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("arapaima");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
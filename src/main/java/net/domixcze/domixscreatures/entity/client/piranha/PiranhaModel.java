package net.domixcze.domixscreatures.entity.client.piranha;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PiranhaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class PiranhaModel extends GeoModel<PiranhaEntity> {
    @Override
    public Identifier getModelResource(PiranhaEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/piranha.geo.json");
    }

    @Override
    public Identifier getTextureResource(PiranhaEntity animatable) {
        return switch (animatable.getVariant()) {
            case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/piranha_blue.png");
            case GREEN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/piranha_green.png");
            case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/piranha_black.png");
        };
    }

    @Override
    public Identifier getAnimationResource(PiranhaEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/piranha.animation.json");
    }

    @Override
    public void setCustomAnimations(PiranhaEntity entity, long instanceId, AnimationState<PiranhaEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("piranha");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.client.carp;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CarpEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class CarpModel extends GeoModel<CarpEntity> {
    @Override
    public Identifier getModelResource(CarpEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/carp.geo.json");
    }

    @Override
    public Identifier getTextureResource(CarpEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/carp.png");
    }

    @Override
    public Identifier getAnimationResource(CarpEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/carp.animation.json");
    }

    @Override
    public void setCustomAnimations(CarpEntity entity, long instanceId, AnimationState<CarpEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = this.getAnimationProcessor().getBone("carp");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
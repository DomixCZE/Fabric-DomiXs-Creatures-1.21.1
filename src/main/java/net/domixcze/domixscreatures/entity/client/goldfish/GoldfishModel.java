package net.domixcze.domixscreatures.entity.client.goldfish;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.GoldfishEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class GoldfishModel extends GeoModel<GoldfishEntity> {
    @Override
    public Identifier getModelResource(GoldfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/goldfish.geo.json");
    }

    @Override
    public Identifier getTextureResource(GoldfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/goldfish.png");
    }

    @Override
    public Identifier getAnimationResource(GoldfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/goldfish.animation.json");
    }

    @Override
    public void setCustomAnimations(GoldfishEntity entity, long instanceId, AnimationState<GoldfishEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("goldfish");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
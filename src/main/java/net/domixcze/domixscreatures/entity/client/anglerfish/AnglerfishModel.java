package net.domixcze.domixscreatures.entity.client.anglerfish;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AnglerfishEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class AnglerfishModel extends GeoModel<AnglerfishEntity> {
    @Override
    public Identifier getModelResource(AnglerfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/anglerfish.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnglerfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/anglerfish.png");
    }

    @Override
    public Identifier getAnimationResource(AnglerfishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/anglerfish.animation.json");
    }

    @Override
    public void setCustomAnimations(AnglerfishEntity entity, long instanceId, AnimationState<AnglerfishEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("anglerfish");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
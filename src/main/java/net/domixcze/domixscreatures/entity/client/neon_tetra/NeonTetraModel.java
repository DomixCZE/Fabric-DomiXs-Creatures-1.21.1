package net.domixcze.domixscreatures.entity.client.neon_tetra;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.NeonTetraEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class NeonTetraModel extends GeoModel<NeonTetraEntity> {
    @Override
    public Identifier getModelResource(NeonTetraEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/neon_tetra.geo.json");
    }

    @Override
    public Identifier getTextureResource(NeonTetraEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/neon_tetra.png");
    }

    @Override
    public Identifier getAnimationResource(NeonTetraEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/neon_tetra.animation.json");
    }

    @Override
    public void setCustomAnimations(NeonTetraEntity entity, long instanceId, AnimationState<NeonTetraEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("neon_tetra");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
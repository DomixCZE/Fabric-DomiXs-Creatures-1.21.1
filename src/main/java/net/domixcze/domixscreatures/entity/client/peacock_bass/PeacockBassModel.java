package net.domixcze.domixscreatures.entity.client.peacock_bass;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PeacockBassEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class PeacockBassModel extends GeoModel<PeacockBassEntity> {
    @Override
    public Identifier getModelResource(PeacockBassEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/peacock_bass.geo.json");
    }

    @Override
    public Identifier getTextureResource(PeacockBassEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/peacock_bass.png");
    }

    @Override
    public Identifier getAnimationResource(PeacockBassEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/peacock_bass.animation.json");
    }

    @Override
    public void setCustomAnimations(PeacockBassEntity entity, long instanceId, AnimationState<PeacockBassEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("peacock_bass");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
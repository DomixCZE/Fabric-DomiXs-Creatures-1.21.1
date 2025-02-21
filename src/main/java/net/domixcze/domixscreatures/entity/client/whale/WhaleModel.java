package net.domixcze.domixscreatures.entity.client.whale;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WhaleEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class WhaleModel extends GeoModel<WhaleEntity> {
    @Override
    public Identifier getModelResource(WhaleEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "geo/whale.geo.json");
    }

    @Override
    public Identifier getTextureResource(WhaleEntity animatable) {
        return animatable.getVariant() == WhaleVariants.ALBINO
                ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_albino.png")
                : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale.png");
    }

    @Override
    public Identifier getAnimationResource(WhaleEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "animations/whale.animation.json");
    }

    @Override
    public void setCustomAnimations(WhaleEntity entity, long instanceId, AnimationState<WhaleEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("whale");

        if (bone != null) {
            if (entity.isBeached()) {
                bone.setRotX(0);
                bone.setRotY(0);
                bone.setRotZ(0);
            } else {
                float targetPitch = -entity.getPitch(animationState.getPartialTick()) * ((float)Math.PI / 180F);

                bone.setRotX(targetPitch);
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.client.magma_ball;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.MagmaBallEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class MagmaBallModel extends GeoModel<MagmaBallEntity> {
    @Override
    public Identifier getModelResource(MagmaBallEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/magma_ball.geo.json");
    }

    @Override
    public Identifier getTextureResource(MagmaBallEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/magma_ball.png");
    }

    @Override
    public Identifier getAnimationResource(MagmaBallEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/magma_ball.animation.json");
    }

    @Override
    public void setCustomAnimations(MagmaBallEntity entity, long instanceId, AnimationState<MagmaBallEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("magma_ball");

        if (bone != null) {
            float pitch = -entity.getPitch(animationState.getPartialTick()) * ((float) Math.PI / 180F);
            float yaw = -entity.getYaw(animationState.getPartialTick()) * ((float) Math.PI / 180F);

            bone.setRotX(bone.getRotX() + pitch);
            bone.setRotY(bone.getRotY() + yaw);
        }
    }
}
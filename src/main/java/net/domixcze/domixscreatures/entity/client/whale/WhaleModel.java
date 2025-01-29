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

        int customNumber = animatable.getBarnacleCount();
        return switch (customNumber) {
            case 0 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_9.png");
            case 1 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_8.png");
            case 2 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_7.png");
            case 3 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_6.png");
            case 4 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_5.png");
            case 5 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_4.png");
            case 6 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_3.png");
            case 7 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_2.png");
            case 8 -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_1.png");
            default -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/whale_barnacles_0.png");
        };
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
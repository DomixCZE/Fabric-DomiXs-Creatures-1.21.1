package net.domixcze.domixscreatures.entity.client.shark;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.SharkEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class SharkModel extends GeoModel<SharkEntity> {
    @Override
    public Identifier getModelResource(SharkEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/shark.geo.json");
    }

    @Override
    public Identifier getTextureResource(SharkEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/shark.png");
    }

    @Override
    public Identifier getAnimationResource(SharkEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/shark.animation.json");
    }

    @Override
    public void setCustomAnimations(SharkEntity entity, long instanceId, AnimationState<SharkEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("shark");

        if (bone != null) {
            if (entity.isBeached()) {
                bone.setRotX(0);
                bone.setRotY(0);
                bone.setRotZ(0);
            }
        }
    }
}
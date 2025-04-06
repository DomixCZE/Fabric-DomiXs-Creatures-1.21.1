/*package net.domixcze.domixscreatures.entity.client.test;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.TestEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class TestModel extends GeoModel<TestEntity> {
    @Override
    public Identifier getModelResource(TestEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "geo/test.geo.json");
    }

    @Override
    public Identifier getTextureResource(TestEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/test.png");
    }

    @Override
    public Identifier getAnimationResource(TestEntity animatable) {
        return new Identifier(DomiXsCreatures.MOD_ID, "animations/test.animation.json");
    }

    @Override
    public void setCustomAnimations(TestEntity entity, long instanceId, AnimationState<TestEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) getAnimationProcessor().getBone("placeholder_george");

        if (bone != null) {
            float pitch = -entity.getPitch(animationState.getPartialTick()) * ((float) Math.PI / 180F);
            float yaw = -entity.getYaw(animationState.getPartialTick()) * ((float) Math.PI / 180F);

            bone.setRotX(pitch);
            bone.setRotY(yaw);
        }
    }
}*/
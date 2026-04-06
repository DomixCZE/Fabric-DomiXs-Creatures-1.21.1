package net.domixcze.domixscreatures.entity.client.owl;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.OwlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class OwlModel extends GeoModel<OwlEntity> {
    @Override
    public Identifier getModelResource(OwlEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_owl.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/owl.geo.json");
    }

    @Override
    public Identifier getTextureResource(OwlEntity animatable) {
        if (animatable.isBaby()) {
            return switch (animatable.getVariant()) {
                case BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_owl_brown.png");
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_owl_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_owl_white.png");
            };
        } else {
            return switch (animatable.getVariant()) {
                case BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/owl_brown.png");
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/owl_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/owl_white.png");
            };
        }
    }

    @Override
    public Identifier getAnimationResource(OwlEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_owl.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/owl.animation.json");
    }

    @Override
    public void setCustomAnimations(OwlEntity animatable, long instanceId, AnimationState<OwlEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        if (animatable.isFlying()) {
            return;
        }

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            float clampedPitch = MathHelper.clamp(entityData.headPitch(), -15.0F, 15.0F);
            float clampedYaw = MathHelper.clamp(entityData.netHeadYaw(), -25.0F, 25.0F);

            head.setRotX(clampedPitch * 0.017453292F);
            head.setRotY(clampedYaw * 0.017453292F);
        }
    }
}

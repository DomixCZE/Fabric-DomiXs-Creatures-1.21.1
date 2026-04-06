package net.domixcze.domixscreatures.entity.client.pigeon;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PigeonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PigeonModel extends GeoModel<PigeonEntity> {
    @Override
    public Identifier getModelResource(PigeonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_pigeon.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/pigeon.geo.json");
    }

    @Override
    public Identifier getTextureResource(PigeonEntity animatable) {
        if (animatable.isBaby()) {
            return switch (animatable.getVariant()) {
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_pigeon_gray.png");
                case LIGHT_GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_pigeon_light_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_pigeon_white.png");
            };
        } else {
            return switch (animatable.getVariant()) {
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/pigeon_gray.png");
                case LIGHT_GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/pigeon_light_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/pigeon_white.png");
            };
        }
    }

    @Override
    public Identifier getAnimationResource(PigeonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_pigeon.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/pigeon.animation.json");
    }

    @Override
    public void setCustomAnimations(PigeonEntity animatable, long instanceId, AnimationState<PigeonEntity> animationState) {
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

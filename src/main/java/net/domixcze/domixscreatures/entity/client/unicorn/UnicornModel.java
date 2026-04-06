package net.domixcze.domixscreatures.entity.client.unicorn;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.UnicornEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class UnicornModel extends GeoModel<UnicornEntity> {
    @Override
    public Identifier getModelResource(UnicornEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/unicorn.geo.json");
    }

    @Override
    public Identifier getTextureResource(UnicornEntity animatable) {
        return switch (animatable.getVariant()) {
            case PINK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_pink.png");
            case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_blue.png");
            case YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_yellow.png");
            case GREEN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/unicorn_green.png");
        };
    }

    @Override
    public Identifier getAnimationResource(UnicornEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/unicorn.animation.json");
    }

    @Override
    public void setCustomAnimations(UnicornEntity animatable, long instanceId, AnimationState<UnicornEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            float clampedPitch = MathHelper.clamp(entityData.headPitch(), -25.0F, 30.0F);
            float clampedYaw = MathHelper.clamp(entityData.netHeadYaw(), -20.0F, 20.0F);

            head.setRotX(clampedPitch * 0.017453292F);
            head.setRotY(clampedYaw * 0.017453292F);
        }
    }
}

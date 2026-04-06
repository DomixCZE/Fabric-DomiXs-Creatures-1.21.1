package net.domixcze.domixscreatures.entity.client.crow;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CrowModel extends GeoModel<CrowEntity> {
    @Override
    public Identifier getModelResource(CrowEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_crow.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/crow.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrowEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_crow.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/crow.png");
    }

    @Override
    public Identifier getAnimationResource(CrowEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_crow.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/crow.animation.json");
    }

    @Override
    public void setCustomAnimations(CrowEntity animatable, long instanceId, AnimationState<CrowEntity> animationState) {
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

package net.domixcze.domixscreatures.entity.client.ancient_skeleton;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AncientSkeletonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AncientSkeletonModel extends GeoModel<AncientSkeletonEntity>  {
    @Override
    public Identifier getModelResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/ancient_skeleton.geo.json");
    }

    @Override
    public Identifier getTextureResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/ancient_skeleton.png");
    }

    @Override
    public Identifier getAnimationResource(AncientSkeletonEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/ancient_skeleton.animation.json");
    }

    @Override
    public void setCustomAnimations(AncientSkeletonEntity animatable, long instanceId, AnimationState<AncientSkeletonEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

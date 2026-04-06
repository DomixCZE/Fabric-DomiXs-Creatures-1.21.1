package net.domixcze.domixscreatures.entity.client.raccoon;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.RaccoonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RaccoonModel extends GeoModel<RaccoonEntity> {
    @Override
    public Identifier getModelResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_raccoon.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/raccoon.geo.json");
    }

    @Override
    public Identifier getTextureResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_raccoon.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/raccoon.png");
    }

    @Override
    public Identifier getAnimationResource(RaccoonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_raccoon.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/raccoon.animation.json");
    }

    @Override
    public void setCustomAnimations(RaccoonEntity animatable, long instanceId, AnimationState<RaccoonEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

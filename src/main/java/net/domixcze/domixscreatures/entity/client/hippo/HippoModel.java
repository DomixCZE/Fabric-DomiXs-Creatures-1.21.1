package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HippoModel extends GeoModel<HippoEntity> {
    @Override
    public Identifier getModelResource(HippoEntity animatable) {

        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_hippo.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/hippo.geo.json");
    }

    @Override
    public Identifier getTextureResource(HippoEntity animatable) {
        if (animatable.getVariant() == HippoVariants.ALBINO) {
            return animatable.isBaby()
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hippo_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hippo_albino.png");
        }

        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hippo.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hippo.png");
    }

    @Override
    public Identifier getAnimationResource(HippoEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_hippo.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/hippo.animation.json");
    }

    @Override
    public void setCustomAnimations(HippoEntity animatable, long instanceId, AnimationState<HippoEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
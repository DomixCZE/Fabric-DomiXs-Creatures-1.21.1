package net.domixcze.domixscreatures.entity.client.cheetah;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CheetahEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CheetahModel extends GeoModel<CheetahEntity> {
    @Override
    public Identifier getModelResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_cheetah.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/cheetah.geo.json");
    }

    @Override
    public Identifier getTextureResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_cheetah.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/cheetah.png");
    }

    @Override
    public Identifier getAnimationResource(CheetahEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_cheetah.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/cheetah.animation.json");
    }

    @Override
    public void setCustomAnimations(CheetahEntity animatable, long instanceId, AnimationState<CheetahEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
package net.domixcze.domixscreatures.entity.client.gorilla;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.GorillaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class GorillaModel extends GeoModel<GorillaEntity> {
    @Override
    public Identifier getModelResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_gorilla.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/gorilla.geo.json");
    }

    @Override
    public Identifier getTextureResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_gorilla.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/gorilla.png");
    }

    @Override
    public Identifier getAnimationResource(GorillaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_gorilla.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/gorilla.animation.json");
    }

    @Override
    public void setCustomAnimations(GorillaEntity animatable, long instanceId, AnimationState<GorillaEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

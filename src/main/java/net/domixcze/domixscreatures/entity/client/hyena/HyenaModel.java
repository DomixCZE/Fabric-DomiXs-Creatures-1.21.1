package net.domixcze.domixscreatures.entity.client.hyena;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HyenaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class HyenaModel extends GeoModel<HyenaEntity> {
    @Override
    public Identifier getModelResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_hyena.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/hyena.geo.json");
    }

    @Override
    public Identifier getTextureResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_hyena.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hyena.png");
    }

    @Override
    public Identifier getAnimationResource(HyenaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_hyena.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/hyena.animation.json");
    }

    @Override
    public void setCustomAnimations(HyenaEntity animatable, long instanceId, AnimationState<HyenaEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
package net.domixcze.domixscreatures.entity.client.boar;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BoarEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BoarModel extends GeoModel<BoarEntity> {
    @Override
    public Identifier getModelResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_boar.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/boar.geo.json");
    }

    @Override
    public Identifier getTextureResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_boar.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/boar.png");
    }

    @Override
    public Identifier getAnimationResource(BoarEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_boar.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/boar.animation.json");
    }

    @Override
    public void setCustomAnimations(BoarEntity animatable, long instanceId, AnimationState<BoarEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
package net.domixcze.domixscreatures.entity.client.moose;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.MooseEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MooseModel extends GeoModel<MooseEntity> {
    @Override
    public Identifier getModelResource(MooseEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_moose.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/moose.geo.json");
    }

    @Override
    public Identifier getTextureResource(MooseEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == MooseVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_moose_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_moose.png");
        } else {
            return animatable.getVariant() == MooseVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/moose_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/moose.png");
        }
    }

    @Override
    public Identifier getAnimationResource(MooseEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_moose.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/moose.animation.json");
    }

    @Override
    public void setCustomAnimations(MooseEntity animatable, long instanceId, AnimationState<MooseEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

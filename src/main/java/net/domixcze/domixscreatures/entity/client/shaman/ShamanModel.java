package net.domixcze.domixscreatures.entity.client.shaman;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.ShamanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ShamanModel extends GeoModel<ShamanEntity> {
    @Override
    public Identifier getModelResource(ShamanEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/shaman.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShamanEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/shaman.png");
    }

    @Override
    public Identifier getAnimationResource(ShamanEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/shaman.animation.json");
    }

    @Override
    public void setCustomAnimations(ShamanEntity animatable, long instanceId, AnimationState<ShamanEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }

        boolean casting = animatable.isCasting();

        GeoBone nonCastingBone = this.getAnimationProcessor().getBone("arms");
        GeoBone castingBone1 = this.getAnimationProcessor().getBone("right_arm");
        GeoBone castingBone2 = this.getAnimationProcessor().getBone("left_arm");

        if (nonCastingBone != null) {
            nonCastingBone.setHidden(casting);
        }

        if (castingBone1 != null) {
            castingBone1.setHidden(!casting);
        }

        if (castingBone2 != null) {
            castingBone2.setHidden(!casting);
        }
    }
}

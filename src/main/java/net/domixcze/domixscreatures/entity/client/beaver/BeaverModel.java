package net.domixcze.domixscreatures.entity.client.beaver;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BeaverEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BeaverModel extends GeoModel<BeaverEntity> {

    @Override
    public Identifier getModelResource(BeaverEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_beaver.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/beaver.geo.json");
    }

    @Override
    public Identifier getTextureResource(BeaverEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == BeaverVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_beaver_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_beaver.png");
        } else {
            return animatable.getVariant() == BeaverVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/beaver_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/beaver.png");
        }
    }

    @Override
    public Identifier getAnimationResource(BeaverEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_beaver.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/beaver.animation.json");
    }

    @Override
    public void setCustomAnimations(BeaverEntity animatable, long instanceId, AnimationState<BeaverEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

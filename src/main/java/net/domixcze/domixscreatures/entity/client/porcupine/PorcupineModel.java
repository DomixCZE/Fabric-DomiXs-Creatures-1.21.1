package net.domixcze.domixscreatures.entity.client.porcupine;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PorcupineEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PorcupineModel extends GeoModel<PorcupineEntity> {
    @Override
    public Identifier getModelResource(PorcupineEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_porcupine.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/porcupine.geo.json");
    }

    @Override
    public Identifier getTextureResource(PorcupineEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == PorcupineVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_porcupine_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_porcupine.png");
        } else {
            return animatable.getVariant() == PorcupineVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/porcupine_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/porcupine.png");
        }
    }

    @Override
    public Identifier getAnimationResource(PorcupineEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_porcupine.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/porcupine.animation.json");
    }

    @Override
    public void setCustomAnimations(PorcupineEntity animatable, long instanceId, AnimationState<PorcupineEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
package net.domixcze.domixscreatures.entity.client.tiger;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.TigerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TigerModel extends GeoModel<TigerEntity> {

    @Override
    public Identifier getModelResource(TigerEntity animatable) {
        if (animatable.isBaby()) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_tiger.geo.json");
        } else if (animatable.getVariant() == TigerVariants.DREAM || animatable.getVariant() == TigerVariants.ALBINO_DREAM) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "geo/dream_tiger.geo.json");
        } else {
            return Identifier.of(DomiXsCreatures.MOD_ID, "geo/tiger.geo.json");
        }
    }

    @Override
    public Identifier getTextureResource(TigerEntity animatable) {
        if (animatable.isBaby()) {
            if (animatable.getVariant() == TigerVariants.ALBINO) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_tiger_albino.png");
            } else {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_tiger.png");
            }
        } else {
            return switch (animatable.getVariant()) {
                case ALBINO -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/tiger_albino.png");
                case DREAM -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/dream_tiger.png");
                case ALBINO_DREAM -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/dream_tiger_albino.png");
                default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/tiger.png");
            };
        }
    }

    @Override
    public Identifier getAnimationResource(TigerEntity animatable) {
        if (animatable.isBaby()) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_tiger.animation.json");
        } else if (animatable.getVariant() == TigerVariants.DREAM || animatable.getVariant() == TigerVariants.ALBINO_DREAM) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "animations/dream_tiger.animation.json");
        } else {
            return Identifier.of(DomiXsCreatures.MOD_ID, "animations/tiger.animation.json");
        }
    }

    @Override
    public void setCustomAnimations(TigerEntity animatable, long instanceId, AnimationState<TigerEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

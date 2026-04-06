package net.domixcze.domixscreatures.entity.client.deer;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.DeerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DeerModel extends GeoModel<DeerEntity> {
    @Override
    public Identifier getModelResource(DeerEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_deer.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/deer.geo.json");
    }

    @Override
    public Identifier getTextureResource(DeerEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == DeerVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_deer_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_deer.png");
        } else {
            return animatable.getVariant() == DeerVariants.ALBINO
                    ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/deer_albino.png")
                    : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/deer.png");
        }
    }

    @Override
    public Identifier getAnimationResource(DeerEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_deer.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/deer.animation.json");
    }

    @Override
    public void setCustomAnimations(DeerEntity animatable, long instanceId, AnimationState<DeerEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}


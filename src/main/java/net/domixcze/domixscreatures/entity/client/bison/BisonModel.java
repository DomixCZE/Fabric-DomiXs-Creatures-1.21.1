package net.domixcze.domixscreatures.entity.client.bison;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BisonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class BisonModel extends GeoModel<BisonEntity> {
    @Override
    public Identifier getModelResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_bison.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/bison.geo.json");
    }

    @Override
    public Identifier getTextureResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_bison.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/bison.png");
    }

    @Override
    public Identifier getAnimationResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_bison.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/bison.animation.json");
    }

    @Override
    public void setCustomAnimations(BisonEntity animatable, long instanceId, AnimationState<BisonEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
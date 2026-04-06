package net.domixcze.domixscreatures.entity.client.anteater;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AnteaterEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AnteaterModel extends GeoModel<AnteaterEntity> {
    @Override
    public Identifier getModelResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_anteater.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/anteater.geo.json");
    }

    @Override
    public Identifier getTextureResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_anteater.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/anteater.png");
    }

    @Override
    public Identifier getAnimationResource(AnteaterEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_anteater.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/anteater.animation.json");
    }

    @Override
    public void setCustomAnimations(AnteaterEntity animatable, long instanceId, AnimationState<AnteaterEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}

package net.domixcze.domixscreatures.entity.client.capybara;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CapybaraEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CapybaraModel extends GeoModel<CapybaraEntity> {
    @Override
    public Identifier getModelResource(CapybaraEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_capybara.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/capybara.geo.json");
    }

    @Override
    public Identifier getTextureResource(CapybaraEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_capybara.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/capybara.png");
    }

    @Override
    public Identifier getAnimationResource(CapybaraEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_capybara.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/capybara.animation.json");
    }

    @Override
    public void setCustomAnimations(CapybaraEntity animatable, long instanceId, AnimationState<CapybaraEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
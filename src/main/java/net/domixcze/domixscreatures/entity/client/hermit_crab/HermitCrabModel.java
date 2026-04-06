package net.domixcze.domixscreatures.entity.client.hermit_crab;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HermitCrabEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class HermitCrabModel extends GeoModel<HermitCrabEntity> {

    @Override
    public Identifier getModelResource(HermitCrabEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/hermit_crab.geo.json");
    }

    @Override
    public Identifier getTextureResource(HermitCrabEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hermit_crab.png");
    }

    @Override
    public Identifier getAnimationResource(HermitCrabEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/hermit_crab.animation.json");
    }

    @Override
    public void setCustomAnimations(HermitCrabEntity animatable, long instanceId, AnimationState<HermitCrabEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone body = getAnimationProcessor().getBone("main");
        GeoBone shell = getAnimationProcessor().getBone("shell");

        if (body != null) {
            body.setHidden(body.getScaleX() <= 0.45f);
        }

        if (shell != null) {
            shell.setHidden(true);
        }
    }
}

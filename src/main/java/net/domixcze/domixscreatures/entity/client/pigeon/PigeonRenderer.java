package net.domixcze.domixscreatures.entity.client.pigeon;

import net.domixcze.domixscreatures.entity.custom.PigeonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PigeonRenderer extends GeoEntityRenderer<PigeonEntity> {
    public PigeonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PigeonModel());
    }

    @Override
    protected float getShadowRadius(PigeonEntity entity) {
        float adultShadowScale = 0.3f;
        float babyShadowScale = 0.2f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
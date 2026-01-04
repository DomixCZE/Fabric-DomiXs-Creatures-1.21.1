package net.domixcze.domixscreatures.entity.client.penguin;

import net.domixcze.domixscreatures.entity.custom.PenguinEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PenguinRenderer extends GeoEntityRenderer<PenguinEntity> {
    public PenguinRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PenguinModel());
    }

    @Override
    protected float getShadowRadius(PenguinEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
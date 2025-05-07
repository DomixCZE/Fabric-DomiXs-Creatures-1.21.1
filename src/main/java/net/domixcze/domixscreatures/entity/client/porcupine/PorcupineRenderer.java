package net.domixcze.domixscreatures.entity.client.porcupine;

import net.domixcze.domixscreatures.entity.custom.PorcupineEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PorcupineRenderer extends GeoEntityRenderer<PorcupineEntity> {
    public PorcupineRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PorcupineModel());
        this.addRenderLayer(new PorcupineQuillLayer(this));
        this.addRenderLayer(new PorcupineSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(PorcupineEntity entity) {
        float adultShadowScale = 0.4f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
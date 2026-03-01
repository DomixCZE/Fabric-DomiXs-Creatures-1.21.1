package net.domixcze.domixscreatures.entity.client.anteater;

import net.domixcze.domixscreatures.entity.custom.AnteaterEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AnteaterRenderer extends GeoEntityRenderer<AnteaterEntity> {
    public AnteaterRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AnteaterModel());
        this.addRenderLayer(new AnteaterSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(AnteaterEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
package net.domixcze.domixscreatures.entity.client.hyena;

import net.domixcze.domixscreatures.entity.custom.HyenaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HyenaRenderer extends GeoEntityRenderer<HyenaEntity> {
    public HyenaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HyenaModel());
        this.addRenderLayer(new HyenaSnowyLayer(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getShadowRadius(HyenaEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
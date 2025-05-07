package net.domixcze.domixscreatures.entity.client.crocodile;

import net.domixcze.domixscreatures.entity.custom.CrocodileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CrocodileRenderer extends GeoEntityRenderer<CrocodileEntity> {
    public CrocodileRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CrocodileModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.addRenderLayer(new CrocodileMossLayer(this));
    }

    @Override
    protected float getShadowRadius(CrocodileEntity entity) {
        float adultShadowScale = 0.8f;
        float babyShadowScale = 0.5f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
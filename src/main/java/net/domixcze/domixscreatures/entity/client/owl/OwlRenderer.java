package net.domixcze.domixscreatures.entity.client.owl;

import net.domixcze.domixscreatures.entity.custom.OwlEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class OwlRenderer extends GeoEntityRenderer<OwlEntity> {
    public OwlRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new OwlModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getShadowRadius(OwlEntity entity) {
        float adultShadowScale = 0.4f;
        float babyShadowScale = 0.2f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}

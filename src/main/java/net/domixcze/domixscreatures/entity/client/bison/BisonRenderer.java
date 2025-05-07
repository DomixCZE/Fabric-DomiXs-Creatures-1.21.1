package net.domixcze.domixscreatures.entity.client.bison;

import net.domixcze.domixscreatures.entity.custom.BisonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BisonRenderer extends GeoEntityRenderer<BisonEntity> {

    public BisonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BisonModel());
        this.addRenderLayer(new BisonSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(BisonEntity entity) {
        float babyShadowScale = 0.5f;
        float adultShadowScale = 1.0f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
package net.domixcze.domixscreatures.entity.client.cheetah;

import net.domixcze.domixscreatures.entity.custom.CheetahEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CheetahRenderer extends GeoEntityRenderer<CheetahEntity> {
    public CheetahRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CheetahModel());
        this.addRenderLayer(new CheetahSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(CheetahEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
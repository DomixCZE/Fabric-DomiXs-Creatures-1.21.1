package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HippoRenderer extends GeoEntityRenderer<HippoEntity> {
    public HippoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HippoModel());
        this.addRenderLayer(new HippoMuddyLayer(this));
    }

    @Override
    protected float getShadowRadius(HippoEntity entity) {
        float adultShadowScale = 1.2f;
        float babyShadowScale = 0.8f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
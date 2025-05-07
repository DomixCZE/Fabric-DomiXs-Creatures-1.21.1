package net.domixcze.domixscreatures.entity.client.iguana;

import net.domixcze.domixscreatures.entity.custom.IguanaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IguanaRenderer extends GeoEntityRenderer<IguanaEntity> {
    public IguanaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new IguanaModel());
        this.addRenderLayer(new IguanaSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(IguanaEntity entity) {
        float adultShadowScale = 0.4f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
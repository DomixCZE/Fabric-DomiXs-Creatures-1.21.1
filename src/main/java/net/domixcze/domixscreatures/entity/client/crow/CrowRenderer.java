package net.domixcze.domixscreatures.entity.client.crow;

import net.domixcze.domixscreatures.entity.custom.CrowEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CrowRenderer extends GeoEntityRenderer<CrowEntity> {
    public CrowRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CrowModel());
    }

    @Override
    protected float getShadowRadius(CrowEntity entity) {
        float adultShadowScale = 0.3f;
        float babyShadowScale = 0.2f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}

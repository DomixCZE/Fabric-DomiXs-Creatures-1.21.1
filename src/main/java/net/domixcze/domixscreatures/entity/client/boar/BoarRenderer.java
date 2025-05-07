package net.domixcze.domixscreatures.entity.client.boar;

import net.domixcze.domixscreatures.entity.custom.BoarEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BoarRenderer extends GeoEntityRenderer<BoarEntity> {
    public BoarRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BoarModel());
        this.addRenderLayer(new BoarSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(BoarEntity entity) {
        float adultShadowScale = 0.6f;
        float babyShadowScale = 0.4f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
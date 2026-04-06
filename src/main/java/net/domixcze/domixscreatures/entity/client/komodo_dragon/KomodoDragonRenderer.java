package net.domixcze.domixscreatures.entity.client.komodo_dragon;

import net.domixcze.domixscreatures.entity.custom.KomodoDragonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class KomodoDragonRenderer extends GeoEntityRenderer<KomodoDragonEntity> {
    public KomodoDragonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new KomodoDragonModel());
        this.addRenderLayer(new KomodoDragonVenomLayer(this));
        this.addRenderLayer(new KomodoDragonSaddleLayer(this));
        this.addRenderLayer(new KomodoDragonSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(KomodoDragonEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}

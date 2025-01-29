package net.domixcze.domixscreatures.entity.client.wisp;

import net.domixcze.domixscreatures.entity.custom.WispEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class WispRenderer extends GeoEntityRenderer<WispEntity> {
    public WispRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WispModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.2F;
    }
}

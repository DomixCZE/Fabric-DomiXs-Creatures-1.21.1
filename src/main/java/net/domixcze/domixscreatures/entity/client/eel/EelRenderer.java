package net.domixcze.domixscreatures.entity.client.eel;

import net.domixcze.domixscreatures.entity.custom.EelEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EelRenderer extends GeoEntityRenderer<EelEntity> {
    public EelRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EelModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.5F;
    }
}
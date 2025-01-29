package net.domixcze.domixscreatures.entity.client.spectral_bat;

import net.domixcze.domixscreatures.entity.custom.SpectralBatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SpectralBatRenderer extends GeoEntityRenderer<SpectralBatEntity> {
    public SpectralBatRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SpectralBatModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.3F;
    }
}
package net.domixcze.domixscreatures.entity.client.shaman;

import net.domixcze.domixscreatures.entity.custom.ShamanEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ShamanRenderer extends GeoEntityRenderer<ShamanEntity> {
    public ShamanRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ShamanModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.5F;
    }
}
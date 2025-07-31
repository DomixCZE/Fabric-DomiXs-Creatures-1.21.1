package net.domixcze.domixscreatures.entity.client.anglerfish;

import net.domixcze.domixscreatures.entity.custom.AnglerfishEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AnglerfishRenderer extends GeoEntityRenderer<AnglerfishEntity> {
    public AnglerfishRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AnglerfishModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.3F;
    }
}
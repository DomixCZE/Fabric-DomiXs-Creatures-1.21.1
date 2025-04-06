package net.domixcze.domixscreatures.entity.client.magma_ball;

import net.domixcze.domixscreatures.entity.custom.MagmaBallEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class MagmaBallRenderer extends GeoEntityRenderer<MagmaBallEntity> {
    public MagmaBallRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MagmaBallModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.4F;
    }
}
package net.domixcze.domixscreatures.entity.client.hermit_crab;

import net.domixcze.domixscreatures.entity.custom.HermitCrabEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HermitCrabRenderer extends GeoEntityRenderer<HermitCrabEntity> {
    public HermitCrabRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HermitCrabModel());
        this.addRenderLayer(new HermitCrabBodyLayer(this));
        this.shadowRadius = 0.3F;
    }
}
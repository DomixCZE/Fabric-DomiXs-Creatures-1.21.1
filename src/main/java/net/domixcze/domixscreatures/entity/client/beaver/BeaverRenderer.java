package net.domixcze.domixscreatures.entity.client.beaver;

import net.domixcze.domixscreatures.entity.custom.BeaverEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BeaverRenderer extends GeoEntityRenderer<BeaverEntity> {
    public BeaverRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BeaverModel());
        this.addRenderLayer(new BeaverSnowyLayer(this));
        this.shadowRadius = 0.3F;
    }
}
package net.domixcze.domixscreatures.entity.client.rat;

import net.domixcze.domixscreatures.entity.custom.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RatRenderer extends GeoEntityRenderer<RatEntity> {
    public RatRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RatModel());
        this.addRenderLayer(new RatSnowyLayer(this));
        this.shadowRadius = 0.4F;
    }
}
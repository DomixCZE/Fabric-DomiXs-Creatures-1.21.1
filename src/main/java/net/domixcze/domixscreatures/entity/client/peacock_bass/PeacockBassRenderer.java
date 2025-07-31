package net.domixcze.domixscreatures.entity.client.peacock_bass;

import net.domixcze.domixscreatures.entity.custom.PeacockBassEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PeacockBassRenderer extends GeoEntityRenderer<PeacockBassEntity> {
    public PeacockBassRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PeacockBassModel());
        this.shadowRadius = 0.3F;
    }
}
package net.domixcze.domixscreatures.entity.client.worm;

import net.domixcze.domixscreatures.entity.custom.WormEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WormRenderer extends GeoEntityRenderer<WormEntity> {
    public WormRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WormModel());
        this.shadowRadius = 0.1F;
    }
}
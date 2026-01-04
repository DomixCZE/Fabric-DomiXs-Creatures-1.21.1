package net.domixcze.domixscreatures.entity.client.carp;

import net.domixcze.domixscreatures.entity.custom.CarpEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CarpRenderer extends GeoEntityRenderer<CarpEntity> {
    public CarpRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CarpModel());
        this.shadowRadius = 0.3F;
    }
}
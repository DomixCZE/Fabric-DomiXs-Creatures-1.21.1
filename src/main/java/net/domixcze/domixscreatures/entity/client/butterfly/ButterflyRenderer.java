package net.domixcze.domixscreatures.entity.client.butterfly;

import net.domixcze.domixscreatures.entity.custom.ButterflyEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ButterflyRenderer extends GeoEntityRenderer<ButterflyEntity> {
    public ButterflyRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ButterflyModel());
        this.addRenderLayer(new ButterflyWingLayer(this));
    }
}
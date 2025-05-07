package net.domixcze.domixscreatures.entity.client.shark;

import net.domixcze.domixscreatures.entity.custom.SharkEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SharkRenderer extends GeoEntityRenderer<SharkEntity> {
    public SharkRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SharkModel());
        this.shadowRadius = 1.0F;
    }
}
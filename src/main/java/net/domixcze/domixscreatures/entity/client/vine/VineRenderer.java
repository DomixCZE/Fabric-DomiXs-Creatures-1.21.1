package net.domixcze.domixscreatures.entity.client.vine;

import net.domixcze.domixscreatures.entity.custom.VineEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VineRenderer extends GeoEntityRenderer<VineEntity> {
    public VineRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new VineModel());
    }
}
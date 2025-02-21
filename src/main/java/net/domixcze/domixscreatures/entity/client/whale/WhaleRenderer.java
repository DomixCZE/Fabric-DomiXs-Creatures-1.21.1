package net.domixcze.domixscreatures.entity.client.whale;

import net.domixcze.domixscreatures.entity.custom.WhaleEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WhaleRenderer extends GeoEntityRenderer<WhaleEntity> {
    public WhaleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WhaleModel());
        this.addRenderLayer(new WhaleBarnacleLayer(this));
        this.shadowRadius = 1.6F;
    }
}
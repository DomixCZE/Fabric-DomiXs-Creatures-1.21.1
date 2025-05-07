package net.domixcze.domixscreatures.entity.client.water_strider;

import net.domixcze.domixscreatures.entity.custom.WaterStriderEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WaterStriderRenderer extends GeoEntityRenderer<WaterStriderEntity> {
    public WaterStriderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WaterStriderModel());
        this.addRenderLayer(new WaterStriderSaddleLayer(this));
        this.shadowRadius = 1.2F;
    }
}
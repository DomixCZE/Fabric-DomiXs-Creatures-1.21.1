package net.domixcze.domixscreatures.entity.client.arapaima;

import net.domixcze.domixscreatures.entity.custom.ArapaimaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ArapaimaRenderer extends GeoEntityRenderer<ArapaimaEntity> {
    public ArapaimaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ArapaimaModel());
        this.shadowRadius = 0.4F;
    }
}
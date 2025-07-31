package net.domixcze.domixscreatures.entity.client.piranha;

import net.domixcze.domixscreatures.entity.custom.PiranhaEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PiranhaRenderer extends GeoEntityRenderer<PiranhaEntity> {
    public PiranhaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new PiranhaModel());
        this.shadowRadius = 0.2F;
    }
}
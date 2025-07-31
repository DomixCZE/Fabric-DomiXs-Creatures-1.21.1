package net.domixcze.domixscreatures.entity.client.neon_tetra;

import net.domixcze.domixscreatures.entity.custom.NeonTetraEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class NeonTetraRenderer extends GeoEntityRenderer<NeonTetraEntity> {
    public NeonTetraRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new NeonTetraModel());
        this.shadowRadius = 0.1F;
    }
}
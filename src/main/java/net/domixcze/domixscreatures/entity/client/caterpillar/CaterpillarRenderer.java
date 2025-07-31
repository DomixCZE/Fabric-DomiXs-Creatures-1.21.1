package net.domixcze.domixscreatures.entity.client.caterpillar;

import net.domixcze.domixscreatures.entity.custom.CaterpillarEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CaterpillarRenderer extends GeoEntityRenderer<CaterpillarEntity> {
    public CaterpillarRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CaterpillarModel());
    }
}
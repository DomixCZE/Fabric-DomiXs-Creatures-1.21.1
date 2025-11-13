package net.domixcze.domixscreatures.entity.client.catfish;

import net.domixcze.domixscreatures.entity.custom.CatfishEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CatfishRenderer extends GeoEntityRenderer<CatfishEntity> {
    public CatfishRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CatfishModel());
        this.shadowRadius = 0.4F;
    }
}
package net.domixcze.domixscreatures.entity.client.freshwater_stingray;

import net.domixcze.domixscreatures.entity.custom.FreshwaterStingrayEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FreshwaterStingrayRenderer extends GeoEntityRenderer<FreshwaterStingrayEntity> {
    public FreshwaterStingrayRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FreshwaterStingrayModel());
        this.shadowRadius = 0.3F;
    }
}
package net.domixcze.domixscreatures.entity.client.deer;

import net.domixcze.domixscreatures.entity.custom.DeerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DeerRenderer extends GeoEntityRenderer<DeerEntity> {
    public DeerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DeerModel());
        this.addRenderLayer(new DeerAntlerLayer(this));
        this.addRenderLayer(new DeerSnowyLayer(this));
        this.shadowRadius = 0.6F;
    }
}
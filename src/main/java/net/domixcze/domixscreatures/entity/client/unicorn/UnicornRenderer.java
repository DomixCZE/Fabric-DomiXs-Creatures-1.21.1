package net.domixcze.domixscreatures.entity.client.unicorn;

import net.domixcze.domixscreatures.entity.custom.UnicornEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class UnicornRenderer extends GeoEntityRenderer<UnicornEntity> {
    public UnicornRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new UnicornModel());
        this.addRenderLayer(new UnicornSnowyLayer(this));
    }
}
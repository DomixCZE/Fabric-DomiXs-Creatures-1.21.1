package net.domixcze.domixscreatures.entity.client.capybara;

import net.domixcze.domixscreatures.entity.custom.CapybaraEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CapybaraRenderer extends GeoEntityRenderer<CapybaraEntity> {
    public CapybaraRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CapybaraModel());
        this.addRenderLayer(new CapybaraSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(CapybaraEntity entity) {
        float adultShadowScale = 0.5f;
        float babyShadowScale = 0.3f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }
}
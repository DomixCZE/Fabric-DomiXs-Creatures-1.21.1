package net.domixcze.domixscreatures.entity.client.moose;

import net.domixcze.domixscreatures.entity.custom.MooseEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MooseRenderer extends GeoEntityRenderer<MooseEntity> {
    public MooseRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MooseModel());
        this.addRenderLayer(new MooseAntlerLayer(this));
        this.addRenderLayer(new MooseSnowyLayer(this));
        this.shadowRadius = 1.0F;
    }
}
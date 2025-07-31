package net.domixcze.domixscreatures.entity.client.betta_fish;

import net.domixcze.domixscreatures.entity.custom.BettaFishEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BettaFishRenderer extends GeoEntityRenderer<BettaFishEntity> {
    public BettaFishRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new BettaFishModel());
        this.shadowRadius = 0.2F;
    }
}
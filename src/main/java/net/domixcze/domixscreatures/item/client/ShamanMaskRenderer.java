package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.ShamanMaskItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ShamanMaskRenderer extends GeoArmorRenderer<ShamanMaskItem> {
    public ShamanMaskRenderer() {
        super(new ShamanMaskModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
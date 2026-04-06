package net.domixcze.domixscreatures.item.client.shaman_mask;

import net.domixcze.domixscreatures.item.custom.armor.ShamanMaskItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ShamanMaskRenderer extends GeoArmorRenderer<ShamanMaskItem> {
    public ShamanMaskRenderer() {
        super(new ShamanMaskModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
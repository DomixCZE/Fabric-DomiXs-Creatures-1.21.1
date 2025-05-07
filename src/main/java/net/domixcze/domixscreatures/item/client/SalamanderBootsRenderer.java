package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.SalamanderBootsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SalamanderBootsRenderer extends GeoArmorRenderer<SalamanderBootsItem> {
    public SalamanderBootsRenderer() {
        super(new SalamanderBootsModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
package net.domixcze.domixscreatures.item.client.salamander_armor;

import net.domixcze.domixscreatures.item.custom.armor.SalamanderHelmetItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SalamanderHelmetRenderer extends GeoArmorRenderer<SalamanderHelmetItem> {
    public SalamanderHelmetRenderer() {
        super(new SalamanderHelmetModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
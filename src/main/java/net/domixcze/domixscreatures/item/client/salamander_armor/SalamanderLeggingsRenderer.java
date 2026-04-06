package net.domixcze.domixscreatures.item.client.salamander_armor;

import net.domixcze.domixscreatures.item.custom.armor.SalamanderLeggingsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SalamanderLeggingsRenderer extends GeoArmorRenderer<SalamanderLeggingsItem> {
    public SalamanderLeggingsRenderer() {
        super(new SalamanderLeggingsModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
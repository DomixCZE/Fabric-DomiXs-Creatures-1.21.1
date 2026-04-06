package net.domixcze.domixscreatures.item.client.salamander_armor;

import net.domixcze.domixscreatures.item.custom.armor.SalamanderChestplateItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class SalamanderChestplateRenderer extends GeoArmorRenderer<SalamanderChestplateItem> {
    public SalamanderChestplateRenderer() {
        super(new SalamanderChestplateModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
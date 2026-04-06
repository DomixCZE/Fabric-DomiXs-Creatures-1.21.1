package net.domixcze.domixscreatures.item.client.wyvern_armor;

import net.domixcze.domixscreatures.item.custom.armor.WyvernScaleHelmetItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleHelmetRenderer extends GeoArmorRenderer<WyvernScaleHelmetItem> {
    public WyvernScaleHelmetRenderer() {
        super(new WyvernScaleHelmetModel());
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.WyvernScaleLeggingsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleLeggingsRenderer extends GeoArmorRenderer<WyvernScaleLeggingsItem> {
    public WyvernScaleLeggingsRenderer() {
        super(new WyvernScaleLeggingsModel());
    }
}
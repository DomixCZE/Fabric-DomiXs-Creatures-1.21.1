package net.domixcze.domixscreatures.item.client.wyvern_armor;

import net.domixcze.domixscreatures.item.custom.armor.WyvernScaleLeggingsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleLeggingsRenderer extends GeoArmorRenderer<WyvernScaleLeggingsItem> {
    public WyvernScaleLeggingsRenderer() {
        super(new WyvernScaleLeggingsModel());
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.WyvernScaleChestplateItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleChestplateRenderer extends GeoArmorRenderer<WyvernScaleChestplateItem> {
    public WyvernScaleChestplateRenderer() {
        super(new WyvernScaleChestplateModel());
    }
}
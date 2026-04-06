package net.domixcze.domixscreatures.item.client.wyvern_armor;

import net.domixcze.domixscreatures.item.custom.armor.WyvernScaleChestplateItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleChestplateRenderer extends GeoArmorRenderer<WyvernScaleChestplateItem> {
    public WyvernScaleChestplateRenderer() {
        super(new WyvernScaleChestplateModel());
    }
}
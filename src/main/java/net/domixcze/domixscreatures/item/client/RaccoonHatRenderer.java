package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.RaccoonHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RaccoonHatRenderer extends GeoArmorRenderer<RaccoonHatItem> {
    public RaccoonHatRenderer() {
        super(new RaccoonHatModel());
    }
}
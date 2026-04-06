package net.domixcze.domixscreatures.item.client.raccoon_hat;

import net.domixcze.domixscreatures.item.custom.armor.RaccoonHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RaccoonHatRenderer extends GeoArmorRenderer<RaccoonHatItem> {
    public RaccoonHatRenderer() {
        super(new RaccoonHatModel());
    }
}
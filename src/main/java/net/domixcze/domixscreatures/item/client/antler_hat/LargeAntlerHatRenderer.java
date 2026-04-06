package net.domixcze.domixscreatures.item.client.antler_hat;

import net.domixcze.domixscreatures.item.custom.armor.LargeAntlerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LargeAntlerHatRenderer extends GeoArmorRenderer<LargeAntlerHatItem> {
    public LargeAntlerHatRenderer() {
        super(new LargeAntlerHatModel());
    }
}
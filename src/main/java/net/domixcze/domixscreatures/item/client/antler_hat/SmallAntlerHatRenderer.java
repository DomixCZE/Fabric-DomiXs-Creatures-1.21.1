package net.domixcze.domixscreatures.item.client.antler_hat;

import net.domixcze.domixscreatures.item.custom.armor.SmallAntlerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SmallAntlerHatRenderer extends GeoArmorRenderer<SmallAntlerHatItem> {
    public SmallAntlerHatRenderer() {
        super(new SmallAntlerHatModel());
    }
}
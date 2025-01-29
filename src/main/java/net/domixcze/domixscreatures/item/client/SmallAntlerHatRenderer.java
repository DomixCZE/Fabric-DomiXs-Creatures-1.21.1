package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.SmallAntlerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SmallAntlerHatRenderer extends GeoArmorRenderer<SmallAntlerHatItem> {
    public SmallAntlerHatRenderer() {
        super(new SmallAntlerHatModel());
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.MediumAntlerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MediumAntlerHatRenderer extends GeoArmorRenderer<MediumAntlerHatItem> {
    public MediumAntlerHatRenderer() {
        super(new MediumAntlerHatModel());
    }
}
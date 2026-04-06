package net.domixcze.domixscreatures.item.client.antler_hat;

import net.domixcze.domixscreatures.item.custom.armor.MediumAntlerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MediumAntlerHatRenderer extends GeoArmorRenderer<MediumAntlerHatItem> {
    public MediumAntlerHatRenderer() {
        super(new MediumAntlerHatModel());
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.AdventurerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AdventurerHatRenderer extends GeoArmorRenderer<AdventurerHatItem> {
    public AdventurerHatRenderer() {
        super(new AdventurerHatModel());
    }
}
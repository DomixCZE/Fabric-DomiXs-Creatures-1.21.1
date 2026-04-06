package net.domixcze.domixscreatures.item.client.adventurer_hat;

import net.domixcze.domixscreatures.item.custom.armor.AdventurerHatItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AdventurerHatRenderer extends GeoArmorRenderer<AdventurerHatItem> {
    public AdventurerHatRenderer() {
        super(new AdventurerHatModel());
    }
}
package net.domixcze.domixscreatures.item.client.crocodile_armor;

import net.domixcze.domixscreatures.item.custom.armor.CrocodileChestplateItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class CrocodileChestplateRenderer extends GeoArmorRenderer<CrocodileChestplateItem> {
    public CrocodileChestplateRenderer() {
        super(new CrocodileChestplateModel());
    }
}
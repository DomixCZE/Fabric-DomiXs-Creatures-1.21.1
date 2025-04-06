package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.CrocodileChestplateItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class CrocodileChestplateRenderer extends GeoArmorRenderer<CrocodileChestplateItem> {
    public CrocodileChestplateRenderer() {
        super(new CrocodileChestplateModel());
    }
}
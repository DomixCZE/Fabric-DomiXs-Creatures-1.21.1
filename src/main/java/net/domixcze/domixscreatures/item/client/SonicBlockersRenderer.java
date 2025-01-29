package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.SonicBlockersItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SonicBlockersRenderer extends GeoArmorRenderer<SonicBlockersItem> {
    public SonicBlockersRenderer() {
        super(new SonicBlockersModel());
    }
}
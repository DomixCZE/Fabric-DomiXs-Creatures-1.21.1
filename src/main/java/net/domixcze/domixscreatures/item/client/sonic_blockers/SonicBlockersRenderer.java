package net.domixcze.domixscreatures.item.client.sonic_blockers;

import net.domixcze.domixscreatures.item.custom.armor.SonicBlockersItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SonicBlockersRenderer extends GeoArmorRenderer<SonicBlockersItem> {
    public SonicBlockersRenderer() {
        super(new SonicBlockersModel());
    }
}
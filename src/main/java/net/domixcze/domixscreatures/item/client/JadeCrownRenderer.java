package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.JadeCrownItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JadeCrownRenderer extends GeoArmorRenderer<JadeCrownItem> {
    public JadeCrownRenderer() {
        super(new JadeCrownModel());
    }
}
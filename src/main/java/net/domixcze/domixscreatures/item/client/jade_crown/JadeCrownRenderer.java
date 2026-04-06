package net.domixcze.domixscreatures.item.client.jade_crown;

import net.domixcze.domixscreatures.item.custom.armor.JadeCrownItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JadeCrownRenderer extends GeoArmorRenderer<JadeCrownItem> {
    public JadeCrownRenderer() {
        super(new JadeCrownModel());
    }
}
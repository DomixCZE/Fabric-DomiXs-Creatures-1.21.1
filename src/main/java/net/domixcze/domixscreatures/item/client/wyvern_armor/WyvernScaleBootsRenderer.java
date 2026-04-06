package net.domixcze.domixscreatures.item.client.wyvern_armor;

import net.domixcze.domixscreatures.item.custom.armor.WyvernScaleBootsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleBootsRenderer extends GeoArmorRenderer<WyvernScaleBootsItem> {
    public WyvernScaleBootsRenderer() {
        super(new WyvernScaleBootsModel());
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.item.custom.WyvernScaleBootsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WyvernScaleBootsRenderer extends GeoArmorRenderer<WyvernScaleBootsItem> {
    public WyvernScaleBootsRenderer() {
        super(new WyvernScaleBootsModel());
    }
}
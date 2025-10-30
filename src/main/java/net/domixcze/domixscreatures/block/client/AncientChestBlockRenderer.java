package net.domixcze.domixscreatures.block.client;

import net.domixcze.domixscreatures.block.entity.AncientChestBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class AncientChestBlockRenderer extends GeoBlockRenderer<AncientChestBlockEntity> {
    public AncientChestBlockRenderer() {
        super(new AncientChestBlockModel());
    }
}
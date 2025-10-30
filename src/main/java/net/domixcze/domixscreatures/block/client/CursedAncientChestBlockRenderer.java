package net.domixcze.domixscreatures.block.client;

import net.domixcze.domixscreatures.block.entity.CursedAncientChestBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CursedAncientChestBlockRenderer extends GeoBlockRenderer<CursedAncientChestBlockEntity> {
    public CursedAncientChestBlockRenderer() {
        super(new CursedAncientChestBlockModel());
    }
}
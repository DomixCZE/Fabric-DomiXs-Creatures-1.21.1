package net.domixcze.domixscreatures.block.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.entity.CursedAncientChestBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CursedAncientChestBlockModel extends GeoModel<CursedAncientChestBlockEntity> {
    @Override
    public Identifier getModelResource(CursedAncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/ancient_chest.geo.json");
    }

    @Override
    public Identifier getTextureResource(CursedAncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/block/ancient_chest.png");
    }

    @Override
    public Identifier getAnimationResource(CursedAncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/ancient_chest.animation.json");
    }
}
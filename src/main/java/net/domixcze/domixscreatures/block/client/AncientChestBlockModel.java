package net.domixcze.domixscreatures.block.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.block.entity.AncientChestBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AncientChestBlockModel extends GeoModel<AncientChestBlockEntity> {
    @Override
    public Identifier getModelResource(AncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/ancient_chest.geo.json");
    }

    @Override
    public Identifier getTextureResource(AncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/block/ancient_chest.png");
    }

    @Override
    public Identifier getAnimationResource(AncientChestBlockEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/ancient_chest.animation.json");
    }
}
package net.domixcze.domixscreatures.entity.client.cave_wyvern;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CaveWyvernModel extends GeoModel<CaveWyvernEntity> {
    @Override
    public Identifier getModelResource(CaveWyvernEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/cave_wyvern.geo.json");
    }

    @Override
    public Identifier getTextureResource(CaveWyvernEntity animatable) {
        if (animatable.isPale()) {
            return Identifier.of("domixs-creatures", "textures/entity/cave_wyvern_pale.png");
        }
        return Identifier.of("domixs-creatures", "textures/entity/cave_wyvern.png");
    }

    @Override
    public Identifier getAnimationResource(CaveWyvernEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/cave_wyvern.animation.json");
    }
}
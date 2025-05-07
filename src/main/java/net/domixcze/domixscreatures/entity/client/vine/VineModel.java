package net.domixcze.domixscreatures.entity.client.vine;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.VineEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class VineModel extends GeoModel<VineEntity> {
    @Override
    public Identifier getModelResource(VineEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/vine.geo.json");
    }

    @Override
    public Identifier getTextureResource(VineEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/vine.png");
    }

    @Override
    public Identifier getAnimationResource(VineEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/vine.animation.json");
    }
}
package net.domixcze.domixscreatures.entity.client.freshwater_stingray;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.FreshwaterStingrayEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class FreshwaterStingrayModel extends GeoModel<FreshwaterStingrayEntity> {
    @Override
    public Identifier getModelResource(FreshwaterStingrayEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/freshwater_stingray.geo.json");
    }

    @Override
    public Identifier getTextureResource(FreshwaterStingrayEntity animatable) {
        return switch (animatable.getVariant()) {
            case YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/freshwater_stingray_yellow.png");
            case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/freshwater_stingray_black.png");
        };
    }

    @Override
    public Identifier getAnimationResource(FreshwaterStingrayEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/freshwater_stingray.animation.json");
    }
}
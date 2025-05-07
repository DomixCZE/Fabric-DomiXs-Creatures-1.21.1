package net.domixcze.domixscreatures.entity.client.water_strider;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WaterStriderEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class WaterStriderModel extends GeoModel<WaterStriderEntity> {
    @Override
    public Identifier getModelResource(WaterStriderEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/water_strider.geo.json");
    }

    @Override
    public Identifier getTextureResource(WaterStriderEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/water_strider.png");
    }

    @Override
    public Identifier getAnimationResource(WaterStriderEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/water_strider.animation.json");
    }
}
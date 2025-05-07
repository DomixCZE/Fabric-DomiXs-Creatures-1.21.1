package net.domixcze.domixscreatures.entity.client.bison;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BisonEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class BisonModel extends GeoModel<BisonEntity> {
    @Override
    public Identifier getModelResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_bison.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/bison.geo.json");
    }

    @Override
    public Identifier getTextureResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_bison.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/bison.png");
    }

    @Override
    public Identifier getAnimationResource(BisonEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_bison.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/bison.animation.json");
    }
}
package net.domixcze.domixscreatures.entity.client.iguana;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.IguanaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class IguanaModel extends GeoModel<IguanaEntity> {

    @Override
    public Identifier getModelResource(IguanaEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "geo/baby_iguana.geo.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "geo/iguana.geo.json");
    }

    @Override
    public Identifier getTextureResource(IguanaEntity animatable) {
        if (animatable.isBaby()) {
            return switch (animatable.getVariant()) {
                case ALBINO -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_albino.png");
                case MELANISTIC -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_black.png");
                case BLUE -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_blue.png");
                default -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana.png");
            };
        } else {
            return switch (animatable.getVariant()) {
                case ALBINO -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/iguana_albino.png");
                case MELANISTIC -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/iguana_black.png");
                case BLUE -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/iguana_blue.png");
                default -> new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/iguana.png");
            };
        }
    }

    @Override
    public Identifier getAnimationResource(IguanaEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "animations/baby_iguana.animation.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "animations/iguana.animation.json");
    }
}
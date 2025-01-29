package net.domixcze.domixscreatures.entity.client.crocodile;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CrocodileEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class CrocodileModel extends GeoModel<CrocodileEntity> {
    @Override
    public Identifier getModelResource(CrocodileEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "geo/baby_crocodile.geo.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "geo/crocodile.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrocodileEntity animatable) {
        if (animatable.isBaby()) {
            return animatable.getVariant() == CrocodileVariants.ALBINO
                    ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_crocodile_albino.png")
                    : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/baby_crocodile.png");
        } else {
            return animatable.getVariant() == CrocodileVariants.ALBINO
                    ? new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/crocodile_albino.png")
                    : new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/crocodile.png");
        }
    }

    @Override
    public Identifier getAnimationResource(CrocodileEntity animatable) {
        return animatable.isBaby()
                ? new Identifier(DomiXsCreatures.MOD_ID, "animations/baby_crocodile.animation.json")
                : new Identifier(DomiXsCreatures.MOD_ID, "animations/crocodile.animation.json");
    }
}
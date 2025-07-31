package net.domixcze.domixscreatures.entity.client.sun_bear;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.SunBearEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class SunBearModel extends GeoModel<SunBearEntity> {
    @Override
    public Identifier getModelResource(SunBearEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_sun_bear.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/sun_bear.geo.json");
    }

    @Override
    public Identifier getTextureResource(SunBearEntity animatable) {
        if (animatable.hasCustomName() && !animatable.isBaby()) {
            String name = normalize(animatable.getName().getString().trim());

            Set<String> specialNames = new HashSet<>();
            specialNames.add("sunny");

            if (specialNames.contains(name.toLowerCase())) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/sun_bear_sunny.png");
            }
        }
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_sun_bear.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/sun_bear.png");
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    @Override
    public Identifier getAnimationResource(SunBearEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_sun_bear.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/sun_bear.animation.json");
    }
}
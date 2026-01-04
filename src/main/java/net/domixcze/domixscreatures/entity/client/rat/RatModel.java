package net.domixcze.domixscreatures.entity.client.rat;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.RatEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class RatModel extends GeoModel<RatEntity> {
    @Override
    public Identifier getModelResource(RatEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_rat.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/rat.geo.json");
    }

    @Override
    public Identifier getTextureResource(RatEntity animatable) {
        if (animatable.hasCustomName() && !animatable.isBaby()) {
            String name = normalize(animatable.getName().getString().trim());

            Set<String> specialNames = new HashSet<>();
            specialNames.add("remy");

            if (specialNames.contains(name.toLowerCase())) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_remy.png");
            }
        }
        if (animatable.isBaby()) {
            return switch (animatable.getVariant()) {
                case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_black.png");
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_white.png");
                case DARK_BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_dark_brown.png");
                case BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_brown.png");
                case BROWN_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_brown_white.png");
                case GRAY_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_gray_white.png");
                case WHITE_BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_white_black.png");
                case WHITE_BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_white_brown.png");
                case WHITE_BLACK_SPOTS -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_rat_white_black_spots.png");
            };
        } else {
            return switch (animatable.getVariant()) {
                case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_black.png");
                case GRAY -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_gray.png");
                case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_white.png");
                case DARK_BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_dark_brown.png");
                case BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_brown.png");
                case BROWN_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_brown_white.png");
                case GRAY_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_gray_white.png");
                case WHITE_BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_white_black.png");
                case WHITE_BROWN -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_white_brown.png");
                case WHITE_BLACK_SPOTS -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/rat_white_black_spots.png");
            };
        }
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    @Override
    public Identifier getAnimationResource(RatEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_rat.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/rat.animation.json");
    }
}
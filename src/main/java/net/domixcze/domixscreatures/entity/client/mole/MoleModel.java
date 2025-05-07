package net.domixcze.domixscreatures.entity.client.mole;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.MoleEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;

public class MoleModel extends GeoModel<MoleEntity> {
    @Override
    public Identifier getModelResource(MoleEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/mole.geo.json");
    }

    @Override
    public Identifier getTextureResource(MoleEntity animatable) {
        if (animatable.hasCustomName()) {
            String name = normalize(animatable.getName().getString().trim());

            Set<String> specialNames = new HashSet<>();
            specialNames.add("krtecek");
            specialNames.add("moley");

            if (specialNames.contains(name.toLowerCase())) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/mole_krtecek.png");
            }
        }
        return animatable.getVariant() == MoleVariants.ALBINO
                ? Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/mole_albino.png")
                : Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/mole.png");
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    @Override
    public Identifier getAnimationResource(MoleEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/mole.animation.json");
    }
}
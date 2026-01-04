package net.domixcze.domixscreatures.entity.client.penguin;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PenguinEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.text.Normalizer;

public class PenguinModel extends GeoModel<PenguinEntity> {

    @Override
    public Identifier getModelResource(PenguinEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_penguin.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/penguin.geo.json");
    }

    @Override
    public Identifier getTextureResource(PenguinEntity animatable) {
        if (animatable.isBaby()) {
            return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_penguin.png");
        }

        if (animatable.hasCustomName()) {
            String name = normalize(animatable.getName().getString().trim()).toLowerCase();

            if (name.equals("linux")) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/penguin_linux.png");
            }

            if (name.equals("pingu")) {
                return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/penguin_pingu.png");
            }
        }

        return Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/penguin.png");
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    @Override
    public Identifier getAnimationResource(PenguinEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_penguin.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/penguin.animation.json");
    }

    @Override
    public void setCustomAnimations(PenguinEntity entity, long instanceId, AnimationState<PenguinEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = this.getAnimationProcessor().getBone("penguin");
        if (bone == null) return;

        if (entity.isSliding()) {
            bone.setRotX((float) Math.toRadians(-90));
            return;
        }

        if (!entity.isTouchingWater()) {
            bone.setRotX(0);
            return;
        }

        float pitchRadians = -entity.getPitch(animationState.getPartialTick()) * ((float)Math.PI / 180F);

        float baseSwimTilt = (float) Math.toRadians(-90);

        bone.setRotX(baseSwimTilt + pitchRadians);
    }
}
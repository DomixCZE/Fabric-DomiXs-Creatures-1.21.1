package net.domixcze.domixscreatures.entity.client.betta_fish;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BettaFishEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

public class BettaFishModel extends GeoModel<BettaFishEntity> {
    @Override
    public Identifier getModelResource(BettaFishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "geo/betta_fish.geo.json");
    }

    @Override
    public Identifier getTextureResource(BettaFishEntity animatable) {
        return switch (animatable.getVariant()) {
            case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_blue.png");
            case RED -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_red.png");
            case WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_white.png");
            case LIGHT_PINK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_light_pink.png");
            case BLUE_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_blue_white.png");
            case RED_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_red_white.png");
            case PINK_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_pink_white.png");
            case GREEN_YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_green_yellow.png");
            case YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_yellow.png");
            case RED_YELLOW -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_red_yellow.png");
            case YELLOW_WHITE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_yellow_white.png");
            case BLACK -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_black.png");
            case BLACK_RED -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_black_red.png");
            case BLUE_RED -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/betta_blue_red.png");
        };
    }

    @Override
    public Identifier getAnimationResource(BettaFishEntity animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID, "animations/betta_fish.animation.json");
    }

    @Override
    public void setCustomAnimations(BettaFishEntity entity, long instanceId, AnimationState<BettaFishEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);

        GeoBone bone = (GeoBone) this.getAnimationProcessor().getBone("betta_fish");

        if (bone != null) {
            if (!entity.isSubmergedInWater() && !entity.isTouchingWater()) {
                bone.setRotZ((float) Math.toRadians(90));
            } else {
                bone.setRotZ(0);
            }
        }
    }
}
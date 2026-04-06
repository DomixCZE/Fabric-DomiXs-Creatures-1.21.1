package net.domixcze.domixscreatures.entity.client.iguana;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.IguanaEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class IguanaModel extends GeoModel<IguanaEntity> {

    @Override
    public Identifier getModelResource(IguanaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "geo/baby_iguana.geo.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "geo/iguana.geo.json");
    }

    @Override
    public Identifier getTextureResource(IguanaEntity animatable) {
        if (animatable.isBaby()) {
            return switch (animatable.getVariant()) {
                case ALBINO -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_albino.png");
                case MELANISTIC -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_black.png");
                case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana_blue.png");
                default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/baby_iguana.png");
            };
        } else {
            return switch (animatable.getVariant()) {
                case ALBINO -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/iguana_albino.png");
                case MELANISTIC -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/iguana_black.png");
                case BLUE -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/iguana_blue.png");
                default -> Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/iguana.png");
            };
        }
    }

    @Override
    public Identifier getAnimationResource(IguanaEntity animatable) {
        return animatable.isBaby()
                ? Identifier.of(DomiXsCreatures.MOD_ID, "animations/baby_iguana.animation.json")
                : Identifier.of(DomiXsCreatures.MOD_ID, "animations/iguana.animation.json");
    }

    @Override
    public void setCustomAnimations(IguanaEntity animatable, long instanceId, AnimationState<IguanaEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = this.getAnimationProcessor().getBone("head_rot");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

        if (head != null && entityData != null) {
            head.setRotX(entityData.headPitch() * 0.017453292F);
            head.setRotY(entityData.netHeadYaw() * 0.017453292F);
        }
    }
}
package net.domixcze.domixscreatures.item.client.raccoon_hat;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.armor.RaccoonHatItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;

public class RaccoonHatModel extends GeoModel<RaccoonHatItem> {

    @Override
    public Identifier getModelResource(RaccoonHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/raccoon_hat.geo.json");
    }

    @Override
    public Identifier getTextureResource(RaccoonHatItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/raccoon_hat.png");
    }

    @Override
    public Identifier getAnimationResource(RaccoonHatItem animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(RaccoonHatItem animatable, long instanceId, AnimationState<RaccoonHatItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone tailBone = this.getAnimationProcessor().getBone("tail");
        if (tailBone == null) {
            return;
        }

        Entity entity = animationState.getData(DataTickets.ENTITY);
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof ArmorStandEntity) {
            resetTailBone(tailBone);
            return;
        }

        Vec3d velocity = livingEntity.getVelocity();

        float bodyYaw = livingEntity.bodyYaw;
        float yawRad = bodyYaw * 0.017453292F;

        double sinYaw = MathHelper.sin(yawRad);
        double negCosYaw = -MathHelper.cos(yawRad);

        float verticalSwing = (float) (-velocity.y * 10.0D);
        verticalSwing = MathHelper.clamp(verticalSwing, -6.0F, 32.0F);

        float forwardMotion = (float) -(velocity.x * sinYaw + velocity.z * negCosYaw) * 100.0F;

        float forwardLift = 0.0F;
        float backwardPush = 0.0F;

        if (forwardMotion > 0.0F) {
            forwardLift = MathHelper.clamp(forwardMotion * 1.15F, 0.0F, 28.0F);
        } else if (forwardMotion < 0.0F) {
            backwardPush = MathHelper.clamp(-forwardMotion * 0.12F, 0.0F, 5.0F);
        }

        float sideSwing = (float) -(velocity.x * negCosYaw - velocity.z * sinYaw) * 100.0F;
        sideSwing = MathHelper.clamp(sideSwing, -20.0F, 20.0F);

        if (livingEntity.isSneaking()) {
            verticalSwing += 25.0F;
        }

        float xRotDeg = 6.0F - forwardLift + backwardPush + verticalSwing;
        float yRotDeg = sideSwing / 2.0F;
        float zRotDeg = -sideSwing / 2.0F;

        tailBone.setRotX((float) Math.toRadians(xRotDeg));
        tailBone.setRotY((float) Math.toRadians(yRotDeg));
        tailBone.setRotZ((float) Math.toRadians(zRotDeg));
    }

    private static void resetTailBone(GeoBone tailBone) {
        tailBone.setRotX(0.0F);
        tailBone.setRotY(0.0F);
        tailBone.setRotZ(0.0F);
    }
}
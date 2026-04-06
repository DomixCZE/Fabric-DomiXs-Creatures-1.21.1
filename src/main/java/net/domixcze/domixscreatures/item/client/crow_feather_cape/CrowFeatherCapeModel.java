package net.domixcze.domixscreatures.item.client.crow_feather_cape;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.item.custom.armor.CrowFeatherCapeItem;
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

public class CrowFeatherCapeModel extends GeoModel<CrowFeatherCapeItem> {

    @Override
    public Identifier getModelResource(CrowFeatherCapeItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"geo/crow_feather_cape.geo.json");
    }

    @Override
    public Identifier getTextureResource(CrowFeatherCapeItem animatable) {
        return Identifier.of(DomiXsCreatures.MOD_ID,"textures/armor/crow_feather_cape.png");
    }

    @Override
    public Identifier getAnimationResource(CrowFeatherCapeItem animatable) {
        return null;
    }

    @Override
    public void setCustomAnimations(CrowFeatherCapeItem animatable, long instanceId, AnimationState<CrowFeatherCapeItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone capeBone = this.getAnimationProcessor().getBone("crow_feather_cape");
        if (capeBone == null) {
            return;
        }

        Entity entity = animationState.getData(DataTickets.ENTITY);
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof ArmorStandEntity) {
            resetCapeBone(capeBone);
            return;
        }

        Vec3d velocity = livingEntity.getVelocity();

        float bodyYaw = livingEntity.bodyYaw;
        float yawRad = bodyYaw * 0.017453292F;

        double sinYaw = MathHelper.sin(yawRad);
        double negCosYaw = -MathHelper.cos(yawRad);

        float verticalSwing = (float) (-velocity.y * 10.0D);
        verticalSwing = MathHelper.clamp(verticalSwing, -6.0F, 32.0F);

        float forwardSwing = (float) -(velocity.x * sinYaw + velocity.z * negCosYaw) * 100.0F;
        forwardSwing = MathHelper.clamp(forwardSwing, 0.0F, 150.0F);

        float sideSwing = (float) (velocity.x * negCosYaw - velocity.z * sinYaw) * 100.0F;
        sideSwing = MathHelper.clamp(sideSwing, -20.0F, 20.0F);

        if (forwardSwing < 0.0F) {
            forwardSwing = 0.0F;
        }

        if (livingEntity.isSneaking()) {
            verticalSwing += 25.0F;
        }

        float xRotDeg = 6.0F + forwardSwing / 2.0F + verticalSwing;
        float yRotDeg = 180.0F - sideSwing / 2.0F;
        float zRotDeg = sideSwing / 2.0F;

        capeBone.setRotX((float) Math.toRadians(xRotDeg));
        capeBone.setRotY((float) Math.toRadians(yRotDeg));
        capeBone.setRotZ((float) Math.toRadians(zRotDeg));
    }

    private static void resetCapeBone(GeoBone capeBone) {
        capeBone.setRotX(0.0F);
        capeBone.setRotY(0.0F);
        capeBone.setRotZ(0.0F);
    }
}
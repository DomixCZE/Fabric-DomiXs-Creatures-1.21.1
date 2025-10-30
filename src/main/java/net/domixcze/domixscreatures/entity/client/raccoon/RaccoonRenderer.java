package net.domixcze.domixscreatures.entity.client.raccoon;

import net.domixcze.domixscreatures.entity.custom.RaccoonEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class RaccoonRenderer extends GeoEntityRenderer<RaccoonEntity> {

    public RaccoonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new RaccoonModel());
        this.addRenderLayer(new RaccoonSnowyLayer(this));
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    protected float getShadowRadius(RaccoonEntity entity) {
        float babyShadowScale = 0.3f;
        float adultShadowScale = 0.5f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, RaccoonEntity entity, GeoBone bone, RenderLayer renderType,
                                  VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay, int colour) {

        ItemStack heldItem = entity.getHeldFood();

        if (!heldItem.isEmpty()) {
            if (bone.getName().equals("left_leg_front") && entity.isWashing()) {
                matrices.push();

                this.getGeoModel().getBone("left_leg_front").ifPresent(paw -> {
                    matrices.translate(paw.getPivotX() / 16f, paw.getPivotY() / 16f, paw.getPivotZ() / 16f);
                    if (paw.getRotX() != 0 || paw.getRotY() != 0 || paw.getRotZ() != 0) {
                        matrices.multiply(new Quaternionf().rotationXYZ(paw.getRotX(), paw.getRotY(), paw.getRotZ()));
                    }
                });

                matrices.translate(0.13D, -0.2D, 0.1D);
                matrices.multiply(new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0),
                        (float) Math.toRadians(-150)));

                matrices.scale(0.7f, 0.7f, 0.7f);

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        entity,
                        heldItem,
                        ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                        false,
                        matrices,
                        bufferSource,
                        entity.getWorld(),
                        packedLight,
                        packedOverlay,
                        entity.getId()
                );

                matrices.pop();
            }

            else if (bone.getName().equals("lower_jaw") && !entity.isWashing()) {
                matrices.push();

                this.getGeoModel().getBone("lower_jaw").ifPresent(jaw -> {
                    matrices.translate(jaw.getPivotX() / 16f, jaw.getPivotY() / 16f, jaw.getPivotZ() / 16f);
                    if (jaw.getRotX() != 0 || jaw.getRotY() != 0 || jaw.getRotZ() != 0) {
                        matrices.multiply(new Quaternionf().rotationXYZ(jaw.getRotX(), jaw.getRotY(), jaw.getRotZ()));
                    }
                });

                matrices.translate(0.0D, 0.07D, -0.25D);
                matrices.multiply(new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0),
                        (float) Math.toRadians(90)));

                matrices.scale(0.7f, 0.7f, 0.7f);

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        entity,
                        heldItem,
                        ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                        false,
                        matrices,
                        bufferSource,
                        entity.getWorld(),
                        packedLight,
                        packedOverlay,
                        entity.getId()
                );

                matrices.pop();
            }
        }

        super.renderRecursively(matrices, entity, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
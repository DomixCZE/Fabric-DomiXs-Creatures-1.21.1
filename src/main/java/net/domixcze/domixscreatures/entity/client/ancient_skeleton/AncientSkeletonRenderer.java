package net.domixcze.domixscreatures.entity.client.ancient_skeleton;

import net.domixcze.domixscreatures.entity.custom.AncientSkeletonEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AncientSkeletonRenderer extends GeoEntityRenderer<AncientSkeletonEntity> {
    public AncientSkeletonRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AncientSkeletonModel());
    }

    @Override
    protected float getShadowRadius(AncientSkeletonEntity entity) {
        return 0.5f;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, AncientSkeletonEntity entity, GeoBone bone, RenderLayer renderType,
                                  VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay, int color) {

        if (bone.getName().equals("weapon")) {
            ItemStack held = entity.getMainHandStack();
            if (!held.isEmpty()) {
                matrices.push();

                this.getGeoModel().getBone("weapon").ifPresent(arm -> {
                    matrices.translate(arm.getPivotX() / 16f, arm.getPivotY() / 16f, arm.getPivotZ() / 16f);
                    if (arm.getRotX() != 0 || arm.getRotY() != 0 || arm.getRotZ() != 0) {
                        matrices.multiply(new Quaternionf().rotationXYZ(arm.getRotX(), arm.getRotY(), arm.getRotZ()));
                    }
                });

                matrices.translate(0f / 16f, -1f / 16f, -2f / 16f);
                matrices.multiply(new Quaternionf().rotationX((float) Math.toRadians(-80)));

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        entity,
                        held,
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
                isReRender, partialTick, packedLight, packedOverlay, color);
    }
}
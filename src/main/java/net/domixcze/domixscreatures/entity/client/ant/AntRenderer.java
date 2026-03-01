package net.domixcze.domixscreatures.entity.client.ant;

import net.domixcze.domixscreatures.entity.custom.AntEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntRenderer extends GeoEntityRenderer<AntEntity> {
    public AntRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AntModel());
    }

    @Override
    protected float getShadowRadius(AntEntity entity) {
        return 0.3f;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, AntEntity entity, GeoBone bone, RenderLayer renderType,
                                  VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay, int colour) {

        if (bone.getName().equals("head") && entity.hasLeaves()) {
            matrices.push();

            this.getGeoModel().getBone("head").ifPresent(paw -> {
                matrices.translate(paw.getPivotX() / 16f, paw.getPivotY() / 16f, paw.getPivotZ() / 16f);
                matrices.multiply(new Quaternionf().rotationXYZ(
                        paw.getRotX() * 0.3f,
                        paw.getRotY() * 0.3f,
                        paw.getRotZ() * 0.3f
                ));
            });

            matrices.translate(0.0D, 0.02D, -0.17D);
            matrices.scale(0.45f, 0.45f, 0.45f);
            matrices.multiply(new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0), (float) Math.toRadians(90)));
            matrices.multiply(new Quaternionf().fromAxisAngleRad(new Vector3f(0, 0, 1), (float) Math.toRadians(180)));

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    entity,
                    entity.getCarriedLeafStack(),
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

        super.renderRecursively(matrices, entity, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
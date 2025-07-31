package net.domixcze.domixscreatures.entity.client.sun_bear;

import net.domixcze.domixscreatures.entity.custom.SunBearEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SunBearRenderer extends GeoEntityRenderer<SunBearEntity> {

    public SunBearRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SunBearModel());
        this.addRenderLayer(new SunBearHoneyLayer(this));
        this.addRenderLayer(new SunBearSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(SunBearEntity entity) {
        float babyShadowScale = 0.4f;
        float adultShadowScale = 0.9f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, SunBearEntity entity, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if (bone.getName().equals("right_paw_front") && entity.isEating()) {
            matrices.push();

            matrices.translate(0.0D, 0.1D, -0.31D);
            matrices.multiply(new Quaternionf().fromAxisAngleRad(new Vector3f(1, 0, 0), (float) Math.toRadians(90)));

            float bobbing = MathHelper.sin(entity.age * 0.26f) * -0.003f;
            matrices.translate(0.0D, bobbing, 0.0D);
            matrices.scale(1.0f, 1.0f, 1.0f);

            MinecraftClient itemRenderer = MinecraftClient.getInstance();

            itemRenderer.getItemRenderer().renderItem(
                    entity,
                    new ItemStack(Items.HONEYCOMB),
                    ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                    false,
                    matrices,
                    bufferSource,
                    entity.getWorld(),
                    packedLight,
                    packedOverlay,
                    entity.getRandom().nextInt()
            );

            matrices.pop();
        }

        super.renderRecursively(matrices, entity, bone, renderType, bufferSource, buffer, isReRender, partialTick,
                packedLight, packedOverlay, colour);
    }
}
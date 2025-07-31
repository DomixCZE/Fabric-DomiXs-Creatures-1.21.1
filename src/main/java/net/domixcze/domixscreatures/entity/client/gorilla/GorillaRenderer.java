package net.domixcze.domixscreatures.entity.client.gorilla;

import net.domixcze.domixscreatures.entity.custom.GorillaEntity;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GorillaRenderer extends GeoEntityRenderer<GorillaEntity> {
    public GorillaRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GorillaModel());
        this.addRenderLayer(new GorillaSnowyLayer(this));
    }

    @Override
    protected float getShadowRadius(GorillaEntity entity) {
        float babyShadowScale = 0.3f;
        float adultShadowScale = 0.8f;
        return entity.isBaby() ? babyShadowScale : adultShadowScale;
    }

    @Override
    public void renderRecursively(MatrixStack matrices, GorillaEntity entity, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        if (bone.getName().equals("right_arm") && entity.isEating()) {
            matrices.push();

            matrices.translate(-0.1D, 0.05D, -0.5D); //x = left/right y = forward/back z = up/down
            matrices.multiply(new Quaternionf().rotationY((float) Math.toRadians(90)));
            matrices.multiply(new Quaternionf().rotationZ((float) Math.toRadians(-60)));
            matrices.multiply(new Quaternionf().rotationX((float) Math.toRadians(30)));

            float bobbing = MathHelper.sin(entity.age * 0.26f) * -0.003f;
            matrices.translate(0.0D, bobbing, 0.0D);
            matrices.scale(1.0f, 1.0f, 1.0f);

            MinecraftClient itemRenderer = MinecraftClient.getInstance();

            itemRenderer.getItemRenderer().renderItem(
                    entity,
                    new ItemStack(ModItems.BANANA),
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
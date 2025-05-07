package net.domixcze.domixscreatures.entity.client.test;

import net.domixcze.domixscreatures.entity.custom.TestEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestRenderer extends GeoEntityRenderer<TestEntity> {
    public TestRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TestModel());
        //this.addRenderLayer(new TestLayer(this));
    }
    @Override
    public void render(TestEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
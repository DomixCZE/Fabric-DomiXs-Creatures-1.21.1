package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HippoRenderer extends GeoEntityRenderer<HippoEntity> {
    public HippoRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HippoModel());
        this.addRenderLayer(new HippoMudLayer(this));
    }

    @Override
    public void render(HippoEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
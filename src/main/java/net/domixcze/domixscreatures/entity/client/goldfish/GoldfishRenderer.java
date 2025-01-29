package net.domixcze.domixscreatures.entity.client.goldfish;

import net.domixcze.domixscreatures.entity.custom.GoldfishEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GoldfishRenderer extends GeoEntityRenderer<GoldfishEntity> {
    public GoldfishRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new GoldfishModel());
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(GoldfishEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

package net.domixcze.domixscreatures.entity.client.mole;

import net.domixcze.domixscreatures.entity.custom.MoleEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MoleRenderer extends GeoEntityRenderer<MoleEntity> {
    public MoleRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MoleModel());
        this.addRenderLayer(new MoleSnowyLayer(this));
        this.shadowRadius = 0.3F;
    }

    @Override
    public void render(MoleEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.8f, 0.8f, 0.8f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
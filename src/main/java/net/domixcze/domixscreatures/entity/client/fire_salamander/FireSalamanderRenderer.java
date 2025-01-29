package net.domixcze.domixscreatures.entity.client.fire_salamander;

import net.domixcze.domixscreatures.entity.custom.FireSalamanderEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FireSalamanderRenderer extends GeoEntityRenderer<FireSalamanderEntity> {

    public FireSalamanderRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new FireSalamanderModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.shadowRadius = 0.7F;
    }

    @Override
    public void render(FireSalamanderEntity entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}

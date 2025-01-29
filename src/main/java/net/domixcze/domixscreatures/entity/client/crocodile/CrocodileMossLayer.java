package net.domixcze.domixscreatures.entity.client.crocodile;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CrocodileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CrocodileMossLayer extends GeoRenderLayer<CrocodileEntity> {
    private static final Identifier MOSS_LAYER = new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/crocodile_overgrown_layer.png");

    public CrocodileMossLayer(GeoRenderer<CrocodileEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, CrocodileEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.isOvergrown()) {
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(MOSS_LAYER);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV,
                1, 1, 1, 1);
        }
    }
}

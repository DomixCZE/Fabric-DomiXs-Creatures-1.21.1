package net.domixcze.domixscreatures.entity.client.water_strider;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WaterStriderEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class WaterStriderSaddleLayer extends GeoRenderLayer<WaterStriderEntity> {

    public WaterStriderSaddleLayer(GeoRenderer<WaterStriderEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, WaterStriderEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isSaddled()) {
            Identifier saddleTexture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/water_strider_saddle.png");

            RenderLayer saddleRenderLayer = RenderLayer.getEntityCutoutNoCull(saddleTexture);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, saddleRenderLayer,
                    bufferSource.getBuffer(saddleRenderLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
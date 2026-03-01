package net.domixcze.domixscreatures.entity.client.cave_wyvern;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class CaveWyvernChainedLayer extends GeoRenderLayer<CaveWyvernEntity> {
    public CaveWyvernChainedLayer(GeoRenderer<CaveWyvernEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, CaveWyvernEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.isChained()) {
            String texturePath = "textures/entity/";
            String snowTexture = "cave_wyvern_chained.png";

            Identifier snowIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + snowTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(snowIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
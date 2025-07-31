package net.domixcze.domixscreatures.entity.client.sun_bear;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.SunBearEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SunBearHoneyLayer extends GeoRenderLayer<SunBearEntity> {
    public SunBearHoneyLayer(GeoRenderer<SunBearEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, SunBearEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.isHoneyed()) {
            String texturePath = "textures/entity/";
            String honeyTexture = animatable.isBaby() ? "sun_bear_honey.png" : "sun_bear_honey.png";

            Identifier honeyIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + honeyTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(honeyIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
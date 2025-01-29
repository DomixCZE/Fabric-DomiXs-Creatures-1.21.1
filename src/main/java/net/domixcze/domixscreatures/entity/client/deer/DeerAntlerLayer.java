package net.domixcze.domixscreatures.entity.client.deer;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.DeerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class DeerAntlerLayer extends GeoRenderLayer<DeerEntity> {
    public DeerAntlerLayer(GeoRenderer<DeerEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, DeerEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isBaby() && animatable.getAntlerSize() != DeerAntlerSize.NONE) {
            String antlerTexture = null;

            switch (animatable.getAntlerSize()) {
                case SMALL -> antlerTexture = "antlers_small.png";
                case MEDIUM -> antlerTexture = "antlers_medium.png";
                case LARGE -> antlerTexture = "antlers_large.png";
                default -> {}
            }

            if (antlerTexture != null) {
                Identifier antlerIdentifier = new Identifier(DomiXsCreatures.MOD_ID, "textures/entity/" + antlerTexture);
                RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(antlerIdentifier);

                getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                        bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
            }
        }
    }
}
package net.domixcze.domixscreatures.entity.client.hermit_crab;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HermitCrabEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HermitCrabBodyLayer extends GeoRenderLayer<HermitCrabEntity> {
    public HermitCrabBodyLayer(GeoRenderer<HermitCrabEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, HermitCrabEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        HermitCrabShapes shape = animatable.getShape();
        HermitCrabVariants color = animatable.getVariant();

        String texturePath = "textures/entity/hc_" + color.asString() + "_" + shape.asString() + "_shell.png";

            Identifier textureIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(textureIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }
}
package net.domixcze.domixscreatures.entity.client.butterfly;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.ButterflyEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class ButterflyPollenLayer extends GeoRenderLayer<ButterflyEntity> {
    public ButterflyPollenLayer(GeoRenderer<ButterflyEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, ButterflyEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.hasPollen()) {

            Identifier pollenTexture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/butterfly_pollen.png");
            RenderLayer pollenLayer = RenderLayer.getEntityCutoutNoCull(pollenTexture);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, pollenLayer,
                    bufferSource.getBuffer(pollenLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
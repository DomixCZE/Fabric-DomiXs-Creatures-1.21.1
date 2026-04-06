package net.domixcze.domixscreatures.entity.client.komodo_dragon;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.KomodoDragonEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class KomodoDragonSaddleLayer extends GeoRenderLayer<KomodoDragonEntity> {

    public KomodoDragonSaddleLayer(GeoRenderer<KomodoDragonEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, KomodoDragonEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.isSaddled()) {
            Identifier saddleTexture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/komodo_dragon_saddle.png");

            RenderLayer saddleRenderLayer = RenderLayer.getEntityCutoutNoCull(saddleTexture);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, saddleRenderLayer,
                    bufferSource.getBuffer(saddleRenderLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
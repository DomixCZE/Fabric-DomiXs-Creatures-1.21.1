package net.domixcze.domixscreatures.entity.client.tiger;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.TigerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TigerSnowyLayer extends GeoRenderLayer<TigerEntity> {
    public TigerSnowyLayer(GeoRenderer<TigerEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, TigerEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.hasSnowLayer()) {
            String texturePath = "textures/entity/";
            String snowTexture;

            if (animatable.isBaby()) {
                snowTexture = "baby_tiger_snowy.png";
            } else {
                if (animatable.getVariant() == TigerVariants.DREAM || animatable.getVariant() == TigerVariants.ALBINO_DREAM) {
                    snowTexture = "dream_tiger_snowy.png";
                } else {
                    snowTexture = "tiger_snowy.png";
                }
            }

            Identifier snowIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + snowTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(snowIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
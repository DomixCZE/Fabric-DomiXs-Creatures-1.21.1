package net.domixcze.domixscreatures.entity.client.anteater;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.AnteaterEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class AnteaterSnowyLayer extends GeoRenderLayer<AnteaterEntity> {
    public AnteaterSnowyLayer(GeoRenderer<AnteaterEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, AnteaterEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.hasSnowLayer()) {
            String texturePath = "textures/entity/";
            String snowTexture = animatable.isBaby() ? "baby_anteater_snowy.png" : "anteater_snowy.png";

            Identifier snowIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + snowTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(snowIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
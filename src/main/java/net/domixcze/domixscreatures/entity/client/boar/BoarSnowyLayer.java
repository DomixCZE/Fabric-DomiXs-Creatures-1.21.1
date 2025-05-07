package net.domixcze.domixscreatures.entity.client.boar;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.BoarEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BoarSnowyLayer extends GeoRenderLayer<BoarEntity> {
    public BoarSnowyLayer(GeoRenderer<BoarEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, BoarEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        if (animatable.hasSnowLayer()) {
            String texturePath = "textures/entity/";
            String snowTexture = animatable.isBaby() ? "baby_boar_snowy.png" : "boar_snowy.png";

            Identifier snowIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + snowTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(snowIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
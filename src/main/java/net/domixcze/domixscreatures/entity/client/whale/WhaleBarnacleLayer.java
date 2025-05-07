package net.domixcze.domixscreatures.entity.client.whale;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WhaleEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class WhaleBarnacleLayer extends GeoRenderLayer<WhaleEntity> {

    public WhaleBarnacleLayer(GeoRenderer<WhaleEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, WhaleEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        int barnacleCount = animatable.getBarnacleCount();
        String barnacleTexture = null;

        switch (barnacleCount) {
            case 9 -> barnacleTexture = "whale_barnacles_9.png";
            case 8 -> barnacleTexture = "whale_barnacles_8.png";
            case 7 -> barnacleTexture = "whale_barnacles_7.png";
            case 6 -> barnacleTexture = "whale_barnacles_6.png";
            case 5 -> barnacleTexture = "whale_barnacles_5.png";
            case 4 -> barnacleTexture = "whale_barnacles_4.png";
            case 3 -> barnacleTexture = "whale_barnacles_3.png";
            case 2 -> barnacleTexture = "whale_barnacles_2.png";
            case 1 -> barnacleTexture = "whale_barnacles_1.png";
            case 0 -> barnacleTexture = "whale_barnacles_0.png";
            default -> {}
        }

        if (barnacleTexture != null) {
            Identifier barnacleIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/" + barnacleTexture);
            RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(barnacleIdentifier);

            getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                    bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
        }
    }
}
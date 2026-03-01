package net.domixcze.domixscreatures.entity.client.wisp;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.WispEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class WispSkullLayer extends GeoRenderLayer<WispEntity> {
    public WispSkullLayer(GeoRenderer<WispEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }
    private static final Identifier SKULL_TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/wisp_skull.png");


    @Override
    public void render(MatrixStack poseStack, WispEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!animatable.isWearingSkull()) {
            return;
        }

        RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(SKULL_TEXTURE);

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }
}
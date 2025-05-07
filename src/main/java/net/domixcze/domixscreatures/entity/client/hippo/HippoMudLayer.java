package net.domixcze.domixscreatures.entity.client.hippo;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HippoEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HippoMudLayer extends GeoRenderLayer<HippoEntity> {
    public HippoMudLayer(GeoRenderer<HippoEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, HippoEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        String texturePath = "textures/entity/";
        String mudTexture = null;

        if (animatable.isMuddy() && animatable.isMudDry()) {
            mudTexture = animatable.isBaby() ? "baby_hippo_dry_mud_layer.png" : "hippo_dry_mud_layer.png";
        } else if (animatable.isMuddy()) {
            mudTexture = animatable.isBaby() ? "baby_hippo_mud_layer.png" : "hippo_mud_layer.png";
        } else {
            return;
        }

        Identifier mudIdentifier = Identifier.of(DomiXsCreatures.MOD_ID, texturePath + mudTexture);
        RenderLayer entityCutoutNoCull = RenderLayer.getEntityCutoutNoCull(mudIdentifier);

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, entityCutoutNoCull,
                bufferSource.getBuffer(entityCutoutNoCull), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }
}
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

public class ButterflyWingLayer extends GeoRenderLayer<ButterflyEntity> {
    public ButterflyWingLayer(GeoRenderer<ButterflyEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, ButterflyEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType,
                       VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        ButterflyVariants variant = animatable.getVariant();

        String textureName = switch (variant) {
            case YELLOW -> "butterfly_yellow.png";
            case ORANGE -> "butterfly_orange.png";
            case WHITE  -> "butterfly_white.png";
            case BLUE   -> "butterfly_blue.png";
            case GREEN  -> "butterfly_green.png";
            case BLACK  -> "butterfly_black.png";
            case TERRACOTTA  -> "butterfly_terracotta.png";
            case BROWN  -> "butterfly_brown.png";
            case CRIMSON  -> "butterfly_crimson.png";
            case PURPLE -> "butterfly_purple.png";
        };

        Identifier texture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/" + textureName);
        RenderLayer wingLayer = RenderLayer.getEntityCutoutNoCull(texture);

        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, wingLayer,
                bufferSource.getBuffer(wingLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }
}
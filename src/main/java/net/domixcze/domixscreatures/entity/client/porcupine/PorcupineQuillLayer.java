package net.domixcze.domixscreatures.entity.client.porcupine;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.PorcupineEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class PorcupineQuillLayer extends GeoRenderLayer<PorcupineEntity> {

    public PorcupineQuillLayer(GeoRenderer<PorcupineEntity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, PorcupineEntity porcupine, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        // Don't render quills if the porcupine is a baby
        if (porcupine.isBaby()) {
            return;
        }

        int quills = porcupine.getQuillsAvailable();
        quills = Math.max(0, Math.min(quills, 5));

        Identifier texture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/porcupine_quills_" + quills + ".png");
        RenderLayer layer = RenderLayer.getEntityCutoutNoCull(texture);

        getRenderer().reRender(getDefaultBakedModel(porcupine), poseStack,
                bufferSource, porcupine, layer, bufferSource.getBuffer(layer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }
}
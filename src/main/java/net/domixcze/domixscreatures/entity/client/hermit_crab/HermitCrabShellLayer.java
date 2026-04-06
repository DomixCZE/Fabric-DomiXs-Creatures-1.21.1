package net.domixcze.domixscreatures.entity.client.hermit_crab;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.custom.HermitCrabEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HermitCrabShellLayer extends GeoRenderLayer<HermitCrabEntity> {
    public HermitCrabShellLayer(GeoRenderer<HermitCrabEntity> entityGeoRenderer) {
        super(entityGeoRenderer);
    }

    @Override
    public void render(MatrixStack poseStack, HermitCrabEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {

        String shape = animatable.getShape().asString();
        String variant = animatable.getVariant().asString();

        Identifier shellTexture = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/hc_" + variant + "_" + shape + "_shell.png");
        RenderLayer shellRenderLayer = RenderLayer.getEntityCutoutNoCull(shellTexture);

        bakedModel.getBone("main").ifPresent(bone -> bone.setHidden(true));

        bakedModel.getBone("shell").ifPresent(shellPivot -> {
            shellPivot.setHidden(false);
            for (GeoBone child : shellPivot.getChildBones()) {
                child.setHidden(!child.getName().equals(shape));
            }
        });

        getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, shellRenderLayer,
                bufferSource.getBuffer(shellRenderLayer), partialTick, packedLight, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);

        bakedModel.getBone("main").ifPresent(bone -> bone.setHidden(false));
        bakedModel.getBone("shell").ifPresent(bone -> {
            for (GeoBone child : bone.getChildBones()) {
                child.setHidden(false);
            }
        });
    }
}

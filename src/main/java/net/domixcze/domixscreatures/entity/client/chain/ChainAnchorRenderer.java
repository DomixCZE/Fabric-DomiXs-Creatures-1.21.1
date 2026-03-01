package net.domixcze.domixscreatures.entity.client.chain;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.entity.client.ModModelLayers;
import net.domixcze.domixscreatures.entity.custom.ChainAnchorEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ChainAnchorRenderer extends EntityRenderer<ChainAnchorEntity> {
    private static final Identifier TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/chain_anchor.png");
    private final ChainAnchorModel model;

    public ChainAnchorRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new ChainAnchorModel(context.getPart(ModModelLayers.CHAIN_ANCHOR));
    }

    @Override
    public void render(ChainAnchorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.scale(-1.0F, -1.0F, 1.0F);

        this.model.setAngles(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(ChainAnchorEntity entity) {
        return TEXTURE;
    }
}
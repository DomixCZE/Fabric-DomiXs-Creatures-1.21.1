package net.domixcze.domixscreatures.entity.client.cave_wyvern;

import net.domixcze.domixscreatures.entity.custom.CaveWyvernEntity;
import net.domixcze.domixscreatures.item.client.chain.IronChainRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.UUID;

public class CaveWyvernRenderer extends GeoEntityRenderer<CaveWyvernEntity> {

    public CaveWyvernRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CaveWyvernModel());
        this.addRenderLayer(new CaveWyvernChainedLayer(this));
    }

    @Override
    protected float getShadowRadius(CaveWyvernEntity entity) {
        return 1.5f;
    }

    @Override
    public void renderFinal(MatrixStack poseStack, CaveWyvernEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, colour);

        UUID ownerUuid = animatable.getChainOwner();
        if (ownerUuid != null) {
            Entity holder = null;
            for (Entity entity : animatable.getWorld().getEntitiesByClass(Entity.class, animatable.getBoundingBox().expand(32.0), e -> e.getUuid().equals(ownerUuid))) {
                holder = entity;
                break;
            }

            if (holder != null) {
                IronChainRenderer.render(animatable, holder, partialTick, poseStack, bufferSource, packedLight);
            }
        }
    }

    @Override
    public boolean shouldRender(CaveWyvernEntity entity, Frustum frustum, double x, double y, double z) {
        if (super.shouldRender(entity, frustum, x, y, z)) return true;

        UUID ownerUuid = entity.getChainOwner();
        if (ownerUuid != null) {
            for (Entity holder : entity.getWorld().getEntitiesByClass(Entity.class, entity.getBoundingBox().expand(32.0), e -> e.getUuid().equals(ownerUuid))) {
                if (frustum.isVisible(holder.getVisibilityBoundingBox())) {
                    return true;
                }
            }
        }
        return false;
    }
}
package net.domixcze.domixscreatures.item.client;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class IronChainRenderer {
    private static final Identifier TEXTURE = Identifier.of(DomiXsCreatures.MOD_ID, "textures/entity/iron_chain.png");
    private static final float HALF_WIDTH = 0.05f;
    private static final float SEGMENT_LENGTH = 0.4f;

    public static void render(LivingEntity leashedEntity, Entity holder, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Vec3d handPosition;
        if (holder instanceof PlayerEntity player) {
            handPosition = player.getLeashPos(tickDelta);
        } else {
            handPosition = holder.getLeashPos(tickDelta).add(0, -0.125, 0);
        }

        double bodyYaw = (double)(leashedEntity.lerpYaw(tickDelta) * 0.017453292F) + (Math.PI / 2.0);
        Vec3d leashOffset = leashedEntity.getLeashOffset(tickDelta);

        double offsetX = Math.cos(bodyYaw) * leashOffset.z + Math.sin(bodyYaw) * leashOffset.x;
        double offsetZ = Math.sin(bodyYaw) * leashOffset.z - Math.cos(bodyYaw) * leashOffset.x;

        double startX = MathHelper.lerp(tickDelta, leashedEntity.prevX, leashedEntity.getX()) + offsetX;
        double startY = MathHelper.lerp(tickDelta, leashedEntity.prevY, leashedEntity.getY()) + leashOffset.y;
        double startZ = MathHelper.lerp(tickDelta, leashedEntity.prevZ, leashedEntity.getZ()) + offsetZ;

        matrices.push();
        matrices.translate(offsetX, leashOffset.y, offsetZ);

        float diffX = (float)(handPosition.x - startX);
        float diffY = (float)(handPosition.y - startY);
        float diffZ = (float)(handPosition.z - startZ);

        float totalDistance = MathHelper.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        @SuppressWarnings("SuspiciousNameCombination")
        float yaw = (float)(MathHelper.atan2(diffX, diffZ));
        float pitch = (float)(-MathHelper.atan2(diffY, MathHelper.sqrt(diffX * diffX + diffZ * diffZ)));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation(pitch));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation((float) (Math.PI / 4.0)));

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        MatrixStack.Entry entry = matrices.peek();

        int segmentCount = MathHelper.ceil(totalDistance / SEGMENT_LENGTH);

        for (int i = 0; i < segmentCount; i++) {
            float zStart = i * SEGMENT_LENGTH;
            float zEnd = Math.min(zStart + SEGMENT_LENGTH, totalDistance);
            float textureFraction = (zEnd - zStart) / SEGMENT_LENGTH;

            drawChainSegment(buffer, entry, zStart, zEnd, true, light, 0.0f, textureFraction);
            drawChainSegment(buffer, entry, zStart, zEnd, false, light, 0.25f, textureFraction);
        }

        matrices.pop();
    }

    private static void drawChainSegment(VertexConsumer buffer, MatrixStack.Entry entry, float zStart, float zEnd, boolean horizontal, int light, float vOffset, float fraction) {
        Matrix4f posMatrix = entry.getPositionMatrix();

        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f + vOffset;
        float maxV = fraction + vOffset;

        float negPos = -HALF_WIDTH;
        float posPos = HALF_WIDTH;

        if (horizontal) {
            addVertex(buffer, posMatrix, entry, negPos, 0, zStart, minU, minV, light);
            addVertex(buffer, posMatrix, entry, negPos, 0, zEnd, minU, maxV, light);
            addVertex(buffer, posMatrix, entry, posPos, 0, zEnd, maxU, maxV, light);
            addVertex(buffer, posMatrix, entry, posPos, 0, zStart, maxU, minV, light);
        } else {
            addVertex(buffer, posMatrix, entry, 0, negPos, zStart, minU, minV, light);
            addVertex(buffer, posMatrix, entry, 0, negPos, zEnd, minU, maxV, light);
            addVertex(buffer, posMatrix, entry, 0, posPos, zEnd, maxU, maxV, light);
            addVertex(buffer, posMatrix, entry, 0, posPos, zStart, maxU, minV, light);
        }
    }

    private static void addVertex(VertexConsumer buffer, Matrix4f posMatrix, MatrixStack.Entry entry, float x, float y, float z, float u, float v, int light) {
        buffer.vertex(posMatrix, x, y, z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
    }
}
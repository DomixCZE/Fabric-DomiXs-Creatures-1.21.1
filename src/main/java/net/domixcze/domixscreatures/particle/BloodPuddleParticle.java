package net.domixcze.domixscreatures.particle;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BloodPuddleParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    private final BlockPos originalBlockPos; // Stores the position of the block underneath
    private static int sequentialSpawnIndex = 0;
    private final float uniqueSequentialYOffset;
    private final float rotationAngle;

    protected BloodPuddleParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;

        this.scale = 0.4f + world.random.nextFloat() * 0.2f; // random puddle size
        this.setMaxAge(300); // 15 seconds
        this.gravityStrength = 0.0f;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;

        this.originalBlockPos = BlockPos.ofFloored(x, y - 0.001, z);

        this.uniqueSequentialYOffset = (sequentialSpawnIndex % 3) * 0.0001f;
        sequentialSpawnIndex++;

        this.rotationAngle = world.random.nextFloat() * (float) (2 * Math.PI);

        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age > 1) {
            BlockState blockState = this.world.getBlockState(originalBlockPos);
            if (blockState.isAir() || blockState.getFluidState().isStill()) {
                this.markDead();
            }
        }

        setSpriteForAge(this.spriteProvider);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d camPos = camera.getPos();
        float particleX = (float)(MathHelper.lerp(tickDelta, this.prevPosX, this.x) - camPos.x);
        float particleY = (float)(MathHelper.lerp(tickDelta, this.prevPosY, this.y) - camPos.y);
        float particleZ = (float)(MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - camPos.z);

        float size = this.getSize(tickDelta);
        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = this.getBrightness(tickDelta);

        float halfSize = size / 2f;
        float finalYOffset = 0.001f + this.uniqueSequentialYOffset;

        float cosRot = MathHelper.cos(this.rotationAngle);
        float sinRot = MathHelper.sin(this.rotationAngle);

        float rotated_bl_x = -halfSize * cosRot - (-halfSize) * sinRot;
        float rotated_bl_z = -halfSize * sinRot + (-halfSize) * cosRot;
        vertexConsumer.vertex(particleX + rotated_bl_x, particleY + finalYOffset, particleZ + rotated_bl_z).texture(minU, maxV).color(red, green, blue, alpha).light(light);

        float rotated_tl_x = -halfSize * cosRot - halfSize * sinRot;
        float rotated_tl_z = -halfSize * sinRot + halfSize * cosRot;
        vertexConsumer.vertex(particleX + rotated_tl_x, particleY + finalYOffset, particleZ + rotated_tl_z).texture(minU, minV).color(red, green, blue, alpha).light(light);

        float rotated_tr_x = halfSize * cosRot - halfSize * sinRot;
        float rotated_tr_z = halfSize * sinRot + halfSize * cosRot;
        vertexConsumer.vertex(particleX + rotated_tr_x, particleY + finalYOffset, particleZ + rotated_tr_z).texture(maxU, minV).color(red, green, blue, alpha).light(light);

        float rotated_br_x = halfSize * cosRot - (-halfSize) * sinRot;
        float rotated_br_z = halfSize * sinRot + (-halfSize) * cosRot;
        vertexConsumer.vertex(particleX + rotated_br_x, particleY + finalYOffset, particleZ + rotated_br_z).texture(maxU, maxV).color(red, green, blue, alpha).light(light);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new BloodPuddleParticle(world, x, y, z, spriteProvider);
        }
    }
}
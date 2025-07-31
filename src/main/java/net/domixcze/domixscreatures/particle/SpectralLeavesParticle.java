package net.domixcze.domixscreatures.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SpectralLeavesParticle extends SpriteBillboardParticle {
    private static final float ACCELERATION = 0.0025F;
    private static final int MAX_AGE = 300;
    private static final float GRAVITY = 0.00075F;
    private static final float WIND_MAGNITUDE = 2.0F;

    private float rotationSpeed;
    private final float windAngle;
    private final float spinAcceleration;

    protected SpectralLeavesParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.setSprite(spriteProvider.getSprite(this.random.nextInt(12), 12));

        this.rotationSpeed = (float) Math.toRadians(this.random.nextBoolean() ? -30.0 : 30.0);
        this.windAngle = this.random.nextFloat();
        this.spinAcceleration = (float) Math.toRadians(this.random.nextBoolean() ? -5.0 : 5.0);

        this.maxAge = MAX_AGE;
        this.gravityStrength = GRAVITY;

        float scale = this.random.nextBoolean() ? 0.05F : 0.075F;
        this.scale = scale;
        this.setBoundingBoxSpacing(scale, scale);

        this.velocityMultiplier = 1.0F;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }

        float ageProgress = (float)(MAX_AGE - this.maxAge);
        float normalizedAge = Math.min(ageProgress / MAX_AGE, 1.0F);

        double windX = Math.cos(Math.toRadians(this.windAngle * 60.0F)) * WIND_MAGNITUDE * Math.pow(normalizedAge, 1.25);
        double windZ = Math.sin(Math.toRadians(this.windAngle * 60.0F)) * WIND_MAGNITUDE * Math.pow(normalizedAge, 1.25);

        this.velocityX += windX * ACCELERATION;
        this.velocityZ += windZ * ACCELERATION;
        this.velocityY -= this.gravityStrength;

        this.rotationSpeed += this.spinAcceleration / 20.0F;
        this.prevAngle = this.angle;
        this.angle += this.rotationSpeed / 20.0F;

        this.move(this.velocityX, this.velocityY, this.velocityZ);

        if (this.onGround || (this.maxAge < MAX_AGE - 1 && (this.velocityX == 0.0 || this.velocityZ == 0.0))) {
            this.markDead();
        }

        if (!this.dead) {
            this.velocityX *= this.velocityMultiplier;
            this.velocityY *= this.velocityMultiplier;
            this.velocityZ *= this.velocityMultiplier;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new SpectralLeavesParticle(world, x, y, z, spriteProvider);
        }
    }
}
package net.domixcze.domixscreatures.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;

public class FallingBloodParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    protected FallingBloodParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.setSprite(spriteProvider.getSprite(world.random));

        this.scale = 0.2f + world.random.nextFloat() * 0.01f;
        this.setMaxAge(300);
        this.gravityStrength = 1.0f;
        this.velocityX = 0;
        this.velocityY = -0.05;
        this.velocityZ = 0;
    }

    protected void updateAgeManual() {
        if (this.maxAge-- <= 0) {
            this.markDead();
        }
    }

    @Override
    public void tick() {
        this.age++;

        setSpriteForAge(this.spriteProvider);

        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        this.updateAgeManual();

        this.velocityY -= this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);

        this.velocityX *= 0.98;
        this.velocityY *= 0.98;
        this.velocityZ *= 0.98;

        BlockPos currentPos = BlockPos.ofFloored(this.x, this.y, this.z);
        FluidState fluidState = this.world.getFluidState(currentPos);
        if (fluidState.getFluid() == Fluids.WATER) {
            this.markDead();
            return;
        }

        //this is very important if it's not here and the particle falls into the void somehow it will eventually crash the game
        if (this.y < this.world.getBottomY() - 100) { // If it's 100 blocks below the world's bottom
            this.markDead();
            return; // Stop ticking immediately
        }

        if (this.age > 1 && this.onGround) {
            this.world.addParticle(ModParticles.BLOOD_PUDDLE, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            this.markDead();
        }
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
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new FallingBloodParticle(world, x, y, z, this.spriteProvider);
        }
    }
}
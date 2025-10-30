package net.domixcze.domixscreatures.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class UnicornDustParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteSet;
    private boolean stuckToBlock = false;

    protected UnicornDustParticle(ClientWorld world, double x, double y, double z,
                                   SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;
        this.setSpriteForAge(spriteSet);

        this.maxAge = 60 + this.random.nextInt(40);
        this.gravityStrength = 0.02f;
        this.velocityMultiplier = 0.98f;
        this.scale = 0.1f + this.random.nextFloat() * 0.05f;
        this.alpha = 0.9f;

        this.velocityY = -0.02;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }

        if (!stuckToBlock) {
            this.prevPosX = this.x;
            this.prevPosY = this.y;
            this.prevPosZ = this.z;

            this.velocityY -= this.gravityStrength;
            this.move(this.velocityX, this.velocityY, this.velocityZ);

            BlockPos blockPos = BlockPos.ofFloored(this.x, this.y - 0.05, this.z);
            if (!world.isAir(blockPos)) {
                this.stuckToBlock = true;
                this.velocityX = 0;
                this.velocityY = 0;
                this.velocityZ = 0;

                this.y = blockPos.getY() + 1.0001;
            }
        }

        if (this.age > this.maxAge - 20) {
            this.alpha = ((float)(this.maxAge - this.age)) / 20.0f;
        }

        this.setSpriteForAge(this.spriteSet);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new UnicornDustParticle(world, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
package net.domixcze.domixscreatures.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class ElectricParticle extends SpriteBillboardParticle {

    protected ElectricParticle(ClientWorld world, double x, double y, double z,
                               SpriteProvider spriteSet, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        this.velocityMultiplier = 1.0F;
        this.gravityStrength = 0.0F;
        this.maxAge = 5;

        this.setVelocity(0.0, 0.0, 0.0);

        this.scale = 0.2f + world.random.nextFloat() * 0.01f;

        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 1.0F;

        this.setSprite(spriteSet.getSprite(this.random.nextInt(4), 4));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {

            return new ElectricParticle(world, x, y, z, this.sprites, velocityX, velocityY, velocityZ);
        }
    }
}
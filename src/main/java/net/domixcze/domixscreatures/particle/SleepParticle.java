package net.domixcze.domixscreatures.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class SleepParticle extends SpriteBillboardParticle {

    private final double baseX;
    private final double baseZ;
    private static final double ZIGZAG_AMPLITUDE = 0.1;  // how far left/right it moves
    private static final double ZIGZAG_FREQUENCY = 0.3;

    protected SleepParticle(ClientWorld level, double x, double y, double z, SpriteProvider spriteSet) {
        super(level, x, y, z, 0, 0.02, 0);  // small upward velocity

        this.baseX = x;
        this.baseZ = z;

        this.velocityMultiplier = 0.6F;
        this.scale *= 0.5F;
        this.maxAge = 20 + this.random.nextInt(10);  // live ~20-30 ticks

        this.setSpriteForAge(spriteSet);

        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 1.0F;
    }

    @Override
    public void tick() {
        super.tick();

        double offsetX = ZIGZAG_AMPLITUDE * Math.sin(this.age * ZIGZAG_FREQUENCY);
        double offsetZ = ZIGZAG_AMPLITUDE * Math.cos(this.age * ZIGZAG_FREQUENCY);

        this.x = baseX + offsetX;
        this.z = baseZ + offsetZ;
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
        public Particle createParticle(SimpleParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new SleepParticle(level, x, y, z, this.sprites);
        }
    }
}
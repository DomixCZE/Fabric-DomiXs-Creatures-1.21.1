package net.domixcze.domixscreatures.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Box;

import java.util.List;

@Environment(EnvType.CLIENT)
public class PoisonBreathParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteSet;

    protected PoisonBreathParticle(ClientWorld world, double x, double y, double z,
                                   SpriteProvider spriteSet, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);
        this.spriteSet = spriteSet;

        this.velocityMultiplier = 0.96f;
        this.maxAge = 30 + this.random.nextInt(20);
        this.scale = 0.4f + this.random.nextFloat() * 0.3f;
        this.alpha = 0.7f;
        this.collidesWithWorld = true;

        this.setSpriteForAge(spriteSet);

        this.velocityX = xd;
        this.velocityY = yd;
        this.velocityZ = zd;
    }

    @Override
    public void tick() {
        double oldVX = this.velocityX;
        double oldVY = this.velocityY;
        double oldVZ = this.velocityZ;

        Box collisionBox = this.getBoundingBox().expand(0.1);
        List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, collisionBox, entity -> !entity.isSpectator());
        if (!list.isEmpty()) {
            this.velocityX = 0;
            this.velocityZ = 0;
        }

        super.tick();

        if (this.dead) return;

        if (oldVX != 0 && this.velocityX == 0) {
            this.velocityZ = oldVZ * 0.8;
            this.velocityY = oldVY * 0.8;
        }
        if (oldVZ != 0 && this.velocityZ == 0) {
            this.velocityX = oldVX * 0.8;
            this.velocityY = oldVY * 0.8;
        }
        if (this.onGround) {
            this.velocityX = oldVX * 0.9;
            this.velocityZ = oldVZ * 0.9;
            this.velocityY = 0;
        }

        float maxScale = 0.75f;
        if (this.scale < maxScale) {
            this.scale += 0.01f;
        }

        if (this.age > this.maxAge / 2) {
            this.alpha = Math.max(0, 1.0f - (float)this.age / (float)this.maxAge);
        }

        this.setSpriteForAge(this.spriteSet);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
            return new PoisonBreathParticle(world, x, y, z, this.sprites, dx, dy, dz);
        }
    }
}
package com.github.klyser8.earthbounds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RubroMadderParticle extends SpriteBillboardParticle {

    private final SpriteProvider spriteProvider;
    protected RubroMadderParticle(ClientWorld clientWorld, double x, double y, double z,
                                  double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, velX, velY, velZ);
        this.spriteProvider = spriteProvider;
        collidesWithWorld = true;
        gravityStrength = 0.5f;
        setVelocity(velX, velY, velZ);
        setSpriteForAge(spriteProvider);
        getBrightness(0.5f + random.nextFloat() / 2);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteForAge(spriteProvider);
        setAlpha(MathHelper.lerp(0.05f, this.alpha, 1.0f));
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld,
                                       double x, double y, double z, double velX, double velY, double velZ) {
            RubroMadderParticle madder = new RubroMadderParticle(clientWorld, x, y, z, velX, velY, velZ,
                    this.spriteProvider);
            madder.setColor(0.5f + clientWorld.random.nextFloat() / 2f, 0, 0);
            return madder;
        }
    }
}

package com.github.klyser8.earthbounds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class GreaseChunkParticle extends AscendingParticle {

    public static final int MAX_AGE = 25;
    public static final int MAX_GROUND_AGE = 10;
    private int groundAge = 0;

    protected GreaseChunkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY,
                                  double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1f, 0.1f, 0.1f,
                velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider,
                1.0F, MAX_AGE, 1f, true);
        red = 1;
        green = 1;
        blue = 1;
        alpha = 0;
    }

    @Override
    public void tick() {
        if (age == 1) {
            alpha = 0.8f;
        }
        if (onGround) {
            groundAge++;
            float a = Math.max(1.0f - ( (groundAge / (float) MAX_GROUND_AGE) + 0.2f), 0);
            setAlpha(a);
            if (groundAge == MAX_GROUND_AGE) {
                dead = true;
            }
        }
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i) {
            return new GreaseChunkParticle(clientWorld, d, e, f, g, h, i, 1.0F, this.spriteProvider);
        }
    }
}
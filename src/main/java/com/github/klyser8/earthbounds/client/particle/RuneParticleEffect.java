package com.github.klyser8.earthbounds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;

public class RuneParticleEffect extends AnimatedParticle {

    public RuneParticleEffect(ClientWorld world, double x, double y, double z,
                              double velX, double velY, double velZ,
                              SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);
        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;
        this.scale = 0.1f;
        this.setSpriteForAge(spriteProvider);
        this.collidesWithWorld = false;
        this.maxAge = 14 + this.random.nextInt(4);
        colorGreen = 0;
        colorBlue = 0;
        colorRed = 1;
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
            return new RuneParticleEffect(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }

}

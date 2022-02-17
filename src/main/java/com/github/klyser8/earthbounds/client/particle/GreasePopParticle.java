package com.github.klyser8.earthbounds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;

public class GreasePopParticle extends AnimatedParticle {

    public GreasePopParticle(ClientWorld world, double x, double y, double z,
                             double velX, double velY, double velZ,
                             SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0);
        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;
        this.scale = 0.15f;
        this.setSpriteForAge(spriteProvider);
        this.collidesWithWorld = false;
        this.maxAge = 14 + this.random.nextInt(2);
        alpha = 0.55f;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getBrightness(float tint) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.world.isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
        }
        return 0;
    }



    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i) {
            return new GreasePopParticle(clientWorld, d, e, f, g, h, i,
                    this.spriteProvider);
        }
    }

}

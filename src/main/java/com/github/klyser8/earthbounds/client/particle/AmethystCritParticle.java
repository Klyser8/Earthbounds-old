package com.github.klyser8.earthbounds.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class AmethystCritParticle extends AnimatedParticle {

    public static final int MAX_AGE = 5;

    protected AmethystCritParticle(ClientWorld world, double x, double y, double z,
                                   double velX, double velY, double velZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 1f);
        velocityX = velX;
        velocityY = velY;
        velocityZ = velZ;
        maxAge = MAX_AGE;
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        if (age > 5) {
            setAlpha(1 - (float) age / maxAge / 2.0f);
        }
        super.tick();
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

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new AmethystCritParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}

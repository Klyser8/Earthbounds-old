package com.github.klyser8.earthbounds.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(EnvType.CLIENT)
public class AmethystShimmerParticle extends AscendingParticle {

    public static final int MAX_AGE = 200;
    boolean shouldAlphaBePositive = false;
    private final float deltaAlpha;

    protected AmethystShimmerParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY,
                                      double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.01f, 0, 0.01f,
                velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider,
                1, MAX_AGE, 0.05f, true);
        maxAge += random.nextInt(100);
        setSpriteForAge(spriteProvider);
        red = 1;
        green = 1;
        blue = 1;
        alpha = 0.8f;
        deltaAlpha = random.nextFloat() / 10 + 0.05f;
    }

    @Override
    public void tick() {
        float a;
        if (alpha <= 0.25) {
            shouldAlphaBePositive = true;
        } else if (alpha >= 1) {
            shouldAlphaBePositive = false;
        }
        if (shouldAlphaBePositive) {
            a = alpha + deltaAlpha;
        } else {
            a = alpha - deltaAlpha;
        }
        if (a > 1) {
            a = 1;
        } else if (a < 0.25) {
            a = 0.25f;
        }
        setAlpha(a);
        if (age > maxAge) {
            dead = true;
        }
        super.tick();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {

        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld,
                                       double d, double e, double f, double g, double h, double i) {
            return new AmethystShimmerParticle(clientWorld, d, e, f, g, h, i, 1.0F, this.spriteProvider);
        }
    }
}

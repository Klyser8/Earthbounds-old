package com.github.klyser8.earthbounds.client.particle;

import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SmallHeartParticle extends SpriteBillboardParticle {

    protected final Vec3f color;

    public SmallHeartParticle(ClientWorld world, double x, double y, double z, double r, double g, double b) {
        super(world, x, y, z);
        this.color = new Vec3f((float) r / 255, (float) g / 255, (float) b / 255);
        red = (float) r / 255;
        green = (float) g / 255;
        blue = (float) b / 255;
        maxAge = 30;
        alpha = 0;
        velocityX = (random.nextFloat() - 0.5) / 10;
        velocityZ = (random.nextFloat() - 0.5) / 10;
        gravityStrength = -0.02f;
    }

    @Override
    public void tick() {
        super.tick();
        if (age <= 8) {
            setAlpha(age / 8f);
        }
        if (age > 20) {
            setAlpha(1 - ((float) age / maxAge) * 3f);
        }
        gravityStrength*=1.1;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public Vec3f getColor() {
        return color;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z,
                                       double r, double g, double b) {
            SmallHeartParticle heartParticle = new SmallHeartParticle(world, x, y, z, r, g, b);
            heartParticle.setSprite(this.spriteProvider);
            return heartParticle;
        }
    }
}

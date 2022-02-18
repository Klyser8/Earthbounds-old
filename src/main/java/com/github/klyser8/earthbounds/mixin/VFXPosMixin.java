package com.github.klyser8.earthbounds.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class VFXPosMixin {

    @Shadow public abstract World getWorld();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow public abstract Vec3d getPos();

    @Redirect(method = "spawnSprintingParticles", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
    private int spawnSprintingParticles(double value) {
        return MixinCallbacks.calculatePosOffset(getWorld(), getBlockPos(), getPos());
    }

    @Redirect(method = "getLandingPos", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
    private int getLandingPos(double value) {
        return MixinCallbacks.calculatePosOffset(getWorld(), getBlockPos(), getPos());
    }
}
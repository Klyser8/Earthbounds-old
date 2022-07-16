package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.MixinCallbacks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class VFXPosMixin {

    @Shadow public abstract World getWorld();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow public abstract Vec3d getPos();

    @Shadow protected abstract BlockPos getPosWithYOffset(float offset);

    @ModifyArg(method = "spawnSprintingParticles", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
    private double spawnCorrectSprintingParticles(double y) {
        return MixinCallbacks.calculateSprintOffset(getWorld(), getPos());
    }

    @ModifyArg(method = "getLandingPos", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;getPosWithYOffset(F)Lnet/minecraft/util/math/BlockPos;", ordinal = 0))
    private float getLandingPos(float offset) {
        return MixinCallbacks.calculateLandOffset(getWorld(), getPos());
    }
}
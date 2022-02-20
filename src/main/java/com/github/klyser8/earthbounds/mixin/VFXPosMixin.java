package com.github.klyser8.earthbounds.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class VFXPosMixin {

    @Shadow public abstract World getWorld();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow public abstract Vec3d getPos();

    @ModifyArg(method = "spawnSprintingParticles", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
    private double spawnCorrectSprintingParticles(double y) {
        return calculatePosOffset();
    }

    @ModifyArg(method = "getLandingPos", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
    private double getLandingPos(double value) {
        return calculatePosOffset();
    }

    public double calculatePosOffset() {
        BlockState state = getWorld().getBlockState(getBlockPos());
        VoxelShape collisionShape = state.getOutlineShape(getWorld(), getBlockPos());
        if (getWorld().isAir(getBlockPos())
                || collisionShape.isEmpty()
                || collisionShape.getBoundingBox().maxY > 0.2) {
            return getPos().y - (double)0.2f;
        } else {
            return getPos().y;
        }
    }
}
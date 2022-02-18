package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
abstract class VelocityAffectingPosMixin {

    @Shadow
    public abstract Vec3d getPos();

    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract World getWorld();

    @Inject(at = @At("HEAD"), method = "getVelocityAffectingPos", cancellable = true)
    private void getVelocityAffectingBlockPos(CallbackInfoReturnable<BlockPos> cir) {
        calculateVelocityAffectingPos(getPos(), getBoundingBox(), getWorld(), cir);
    }

    private void calculateVelocityAffectingPos(Vec3d pos, Box box, World world,
                                                     CallbackInfoReturnable<BlockPos> cir) {
        BlockPos blockPos = new BlockPos(pos.x, box.minY, pos.z);
        if (world.getBlockState(blockPos).getBlock() instanceof GlowGreaseSplatBlock) {
            cir.setReturnValue(blockPos);
        } else {
            cir.setReturnValue(blockPos.add(0, -0.5000001, 0));
        }
    }

}
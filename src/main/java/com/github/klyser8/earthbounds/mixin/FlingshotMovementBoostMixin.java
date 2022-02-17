package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.MixinCallbacks;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class FlingshotMovementBoostMixin extends AbstractClientPlayerEntity {

    @Shadow public Input input;

    public FlingshotMovementBoostMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target =
            "Lnet/minecraft/client/network/ClientPlayerEntity;ticksLeftToDoubleTapSprint:I",
            ordinal = 1, opcode = Opcodes.PUTFIELD))
    private void addFlingshotSpeedBoost(CallbackInfo ci) {
        MixinCallbacks.applyFlingshotMovementBoost(getActiveItem(), input);
    }

}

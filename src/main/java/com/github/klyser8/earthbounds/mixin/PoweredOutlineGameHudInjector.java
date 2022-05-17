package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.MixinCallbacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class PoweredOutlineGameHudInjector {

    private static final Identifier POWERED_OUTLINE = new Identifier(Earthbounds.MOD_ID,
            "textures/misc/powered_outline.png");

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderOverlay(Identifier texture, float opacity);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getFrozenTicks()I")))
    private void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (MixinCallbacks.shouldRenderPowerOutline(client.player)) {
            renderOverlay(POWERED_OUTLINE, Math.min(1.0f, client.player.getHungerManager().getSaturationLevel() / 20.0f));
        }
    }

}

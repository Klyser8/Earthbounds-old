package com.github.klyser8.earthbounds.mixin;

import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(StatusEffects.class)
public class NauseaStatusEffectColorMixin {



    @ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=nausea")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/effect/StatusEffect;<init>(Lnet/minecraft/entity/effect/StatusEffectCategory;I)V",
                    ordinal = 0))
    private static int changeColor(int color) {
        return 13825638;
    }

}

package com.github.klyser8.earthbounds.mixin;


import com.github.klyser8.earthbounds.MixinCallbacks;
import com.github.klyser8.earthbounds.entity.mob.Earthen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MiningToolItem.class)
public abstract class PickaxeDamageInjector extends ToolItem {

    public PickaxeDamageInjector(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @ModifyArgs(method = "postHit", at = @At(value = "INVOKE",
    target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"))
    public void calculateToolDamage(Args args, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MixinCallbacks.calculatePickaxeDamage(args, stack, target);
    }
}

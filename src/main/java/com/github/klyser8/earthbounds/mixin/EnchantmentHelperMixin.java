package com.github.klyser8.earthbounds.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    //most compat way to make enchantment table apply our enchantment properly
    @Inject(method = "getPossibleEntries",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void applyEnchantmentsCorrectly(int power, ItemStack stack, boolean treasureAllowed,
                                                   CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir,
                                                   List<EnchantmentLevelEntry> list, Item item,
                                                   boolean bl, Iterator<Enchantment> var6, Enchantment enchantment) {
        MixinCallbacks.canEnchant(stack, list, enchantment);
    }

}

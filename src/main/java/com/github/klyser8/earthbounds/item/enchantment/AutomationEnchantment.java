package com.github.klyser8.earthbounds.item.enchantment;

import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class AutomationEnchantment extends Enchantment {

    public AutomationEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]
                {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return true;
    }

    /**
     * Enchantment can only be applied to flingshots.
     */
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof FlingshotItem;
    }

}

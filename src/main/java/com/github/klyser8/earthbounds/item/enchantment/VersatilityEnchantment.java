package com.github.klyser8.earthbounds.item.enchantment;

import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;

public class VersatilityEnchantment extends Enchantment {

    public VersatilityEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]
                {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 15;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
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

    @Override
    protected boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof AutomationEnchantment);
    }

    /**
     * Enchantment can only be applied to flingshots.
     */
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof FlingshotItem || stack.getItem() instanceof BookItem;
    }

}

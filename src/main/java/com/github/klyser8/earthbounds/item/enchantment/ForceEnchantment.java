package com.github.klyser8.earthbounds.item.enchantment;

import com.github.klyser8.earthbounds.item.flingshot.FlingshotItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class ForceEnchantment extends Enchantment {

    private final int BASE_POWER = 1;
    private final int POWER_PER_LEVEl = 12;

    public ForceEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[]
                {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return BASE_POWER + (level - 1) * POWER_PER_LEVEl;
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
        return stack.getItem() instanceof FlingshotItem || stack.getItem() instanceof BookItem;
    }

    public float getExtraForce(int level) {
        return 0.2f * level;
    }

}

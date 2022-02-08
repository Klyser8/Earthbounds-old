package com.github.klyser8.earthbounds.item.enchantment;

import com.github.klyser8.earthbounds.entity.EarthboundEntityGroup;
import com.github.klyser8.earthbounds.entity.Earthen;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class CrumbleEnchantment extends Enchantment {

    private final int BASE_POWER = 1;
    private final int POWER_PER_LEVEl = 8;

    public CrumbleEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return BASE_POWER + (level - 1) * POWER_PER_LEVEl;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        super.onTargetDamaged(user, target, level);
    }

    /**
     * The enchantment clashes with efficiency and silk touch.
     */
    public boolean canAccept(Enchantment other) {
        return !(other instanceof LuckEnchantment) && !(other instanceof SilkTouchEnchantment) &&
                super.canAccept(other);
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
     * Enchantment can only be applied to pickaxes.
     */
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    /**
     * Extra damage to earthen entities is 2.5 * enchantment level
     */
    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return group == EarthboundEntityGroup.EARTHEN ? level * 2.5f : 0f;
    }

}

package com.github.klyser8.earthbounds.item.enchantment;

import com.github.klyser8.earthbounds.entity.mob.EarthboundEntityGroup;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class CrumbleEnchantment extends Enchantment {

    private final int BASE_POWER = 12;
    private final int POWER_PER_LEVEl = 16;

    public CrumbleEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return BASE_POWER + (level - 1) * POWER_PER_LEVEl;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMaxPower(level);
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
        return stack.getItem() instanceof PickaxeItem || stack.getItem() instanceof BookItem;
    }

    /**
     * Extra damage to earthen entities is:
     * - 2.5 * ench level (If enchantment is above level 3)
     * - 4.0 * ench level (Below level 3)
     *
     * Max ench level was decreased from 5 to 3 in 1.1.1, hence this formula.
     */
    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        if (level > 3) {
            return group == EarthboundEntityGroup.EARTHEN ? level * 2.5f : 0f;
        } else {
            return group == EarthboundEntityGroup.EARTHEN ? level * 4.0f : 0f;
        }
    }

}

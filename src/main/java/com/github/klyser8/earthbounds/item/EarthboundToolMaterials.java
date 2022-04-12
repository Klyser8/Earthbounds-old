package com.github.klyser8.earthbounds.item;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum EarthboundToolMaterials implements ToolMaterial {

    COPPER(320, 1, Ingredient.ofItems(Items.COPPER_INGOT));

    private final int itemDurability;
    private final int enchantability;
    private final Ingredient repairIngredient;

    EarthboundToolMaterials(int itemDurability, int enchantability, Ingredient repairIngredient) {
        this.itemDurability = itemDurability;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient;
    }

}

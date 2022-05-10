package com.github.klyser8.earthbounds.registry;

import net.minecraft.item.FoodComponent;
import net.minecraft.util.registry.Registry;

/**
 * IMPORTANT! Redstone foods ({@link com.github.klyser8.earthbounds.item.RedstoneFoodItem} will use
 * the saturation modifier as a static saturation increase, and not a modifier.
 */
public class EarthboundFoodComponents {

    public static final FoodComponent COBBLED_PEBBLE = new FoodComponent.Builder().hunger(1).saturationModifier(2).alwaysEdible().build();
    public static final FoodComponent DEEPSLATE_PEBBLE = new FoodComponent.Builder().hunger(2).saturationModifier(2).alwaysEdible().build();
    public static final FoodComponent ANDESITE_PEBBLE = new FoodComponent.Builder().hunger(3).saturationModifier(2).alwaysEdible().build();
    public static final FoodComponent DIORITE_PEBBLE = new FoodComponent.Builder().hunger(2).saturationModifier(4).alwaysEdible().build();
    public static final FoodComponent GRANITE_PEBBLE = new FoodComponent.Builder().hunger(1).saturationModifier(6).alwaysEdible().build();
    public static final FoodComponent REDSTONE_PEBBLE = new FoodComponent.Builder().hunger(5).saturationModifier(6).alwaysEdible().build();
    public static final FoodComponent PRIMORDIAL_REDSTONE = new FoodComponent.Builder().hunger(10).saturationModifier(8).alwaysEdible().build();
    public static final FoodComponent POWERED_BEETROOT = new FoodComponent.Builder().hunger(6).saturationModifier(8).alwaysEdible().build();
    public static final FoodComponent BLUSHED_FLINTS = new FoodComponent.Builder().hunger(8).saturationModifier(8).alwaysEdible().build();
    public static final FoodComponent RED_BRICK = new FoodComponent.Builder().hunger(6).saturationModifier(10).alwaysEdible().build();
    public static final FoodComponent CRIMSON_QUARTZ = new FoodComponent.Builder().hunger(4).saturationModifier(12).alwaysEdible().build();

}

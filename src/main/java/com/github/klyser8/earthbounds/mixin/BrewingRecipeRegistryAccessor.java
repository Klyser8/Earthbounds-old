package com.github.klyser8.earthbounds.mixin;

import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Used to register custom potion recipes
 */
@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryAccessor {

    @Invoker("registerItemRecipe")
    static void invokeRegisterItemRecipe(Item input, Item ingredient, Item output) {
        throw new AssertionError();
    }
}
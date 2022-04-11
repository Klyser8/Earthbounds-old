package com.github.klyser8.earthbounds.mixin;

import com.github.klyser8.earthbounds.MixinCallbacks;
import com.github.klyser8.earthbounds.registry.EarthboundItemGroup;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public class RecipeBookGroupInjector {

    @Inject(method = "getGroupForRecipe", at = @At(value = "FIELD", target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;" +
            "CRAFTING_MISC:Lnet/minecraft/client/recipebook/RecipeBookGroup;", opcode = Opcodes.GETSTATIC), cancellable = true)
    private static void mixin(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        MixinCallbacks.insertRecipesInGroups(recipe, cir);
    }

}

package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.mixin.BrewingRecipeRegistryAccessor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundPotions {

    public static final Potion SICKNESS = new Potion(new StatusEffectInstance(StatusEffects.NAUSEA, 900));
    public static final Potion LONG_SICKNESS = new Potion(new StatusEffectInstance(StatusEffects.NAUSEA, 1800));

    public static void register() {
        Registry.register(Registry.POTION, new Identifier(Earthbounds.MOD_ID, "sickness"), SICKNESS);
        Registry.register(Registry.POTION, new Identifier(Earthbounds.MOD_ID, "long_sickness"), LONG_SICKNESS);

        BrewingRecipeRegistryAccessor.invokeRegisterPotionType(EarthboundItems.FLINGING_POTION);
        BrewingRecipeRegistryAccessor.invokeRegisterItemRecipe(
                Items.POTION, EarthboundItems.AMETHYST_DUST, EarthboundItems.FLINGING_POTION);

        BrewingRecipeRegistryAccessor.invokeRegisterPotionRecipe(Potions.AWKWARD, EarthboundItems.GLOW_GREASE, SICKNESS);
        BrewingRecipeRegistryAccessor.invokeRegisterPotionRecipe(SICKNESS, Items.REDSTONE, LONG_SICKNESS);
    }

}

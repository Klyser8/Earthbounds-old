package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundItemGroup {

    public static final ItemGroup PLACEABLES = FabricItemGroupBuilder.build(
            new Identifier(Earthbounds.MOD_ID, "placeables"), () ->
                    new ItemStack(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK));
    public static final ItemGroup COMBAT = FabricItemGroupBuilder.build(
            new Identifier(Earthbounds.MOD_ID, "combat"), () ->
                    new ItemStack(EarthboundItems.FLINGSHOT));
    public static final ItemGroup MISC = FabricItemGroupBuilder.build(
            new Identifier(Earthbounds.MOD_ID, "misc"), () ->
                    new ItemStack(EarthboundItems.CARBORANEA_BUCKET));
    public static final ItemGroup BREWING = FabricItemGroupBuilder.build(
            new Identifier(Earthbounds.MOD_ID, "brewing"), () ->
                    new ItemStack(EarthboundItems.FLINGING_POTION));

}

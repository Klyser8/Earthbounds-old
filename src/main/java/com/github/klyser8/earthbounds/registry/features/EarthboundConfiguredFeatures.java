package com.github.klyser8.earthbounds.registry.features;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.*;

import java.util.List;

public class EarthboundConfiguredFeatures {

    public static ConfiguredFeature<?, ?> OVERWORLD_REDSTONE_FOSSIL;
    public static ConfiguredFeature<?, ?> OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL;
    public static ConfiguredFeature<?, ?> OVERWORLD_GILDED_REDSTONE_FOSSIL;
    public static ConfiguredFeature<?, ?> OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL;

    public static ConfiguredFeature<?, ?> SMALL_COAL_DEN;
    public static ConfiguredFeature<?, ?> GLOW_GREASE_SPLAT;

    //Discard on air chance = Chance that if the block is exposed to air, it is discarded.
    static void register() {
        OVERWORLD_REDSTONE_FOSSIL = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "overworld_redstone_fossil"),
                Feature.REPLACE_SINGLE_BLOCK.configure(
                        new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                                EarthboundBlocks.REDSTONE_FOSSIL_BLOCK.getDefaultState())));
///fill ~-16 ~-12 ~-16 ~16 ~12 ~16 air replace deepslate
        OVERWORLD_GILDED_REDSTONE_FOSSIL = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "overworld_gilded_redstone_fossil"),
                Feature.REPLACE_SINGLE_BLOCK.configure(
                        new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                                EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK.getDefaultState())));

        OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "overworld_deepslate_redstone_fossil"),
                Feature.REPLACE_SINGLE_BLOCK.configure(
                        new EmeraldOreFeatureConfig(Blocks.DEEPSLATE.getDefaultState(),
                                EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK.getDefaultState())));

        OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "overworld_deepslate_gilded_redstone_fossil"),
                Feature.REPLACE_SINGLE_BLOCK.configure(
                        new EmeraldOreFeatureConfig(Blocks.DEEPSLATE.getDefaultState(),
                                EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK.getDefaultState())));

        SMALL_COAL_DEN = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "small_coal_den"),
                EarthboundFeatures.COAL_DEN.configure(new CoalDenFeatureConfig(
                        ConstantIntProvider.create(-8), ConstantIntProvider.create(12)
                )));

        GLOW_GREASE_SPLAT = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "glow_grease_splat"),
                EarthboundFeatures.GLOW_GREASE_SPLAT.configure(new GlowLichenFeatureConfig(
                30, true, false, true, 0.9f, List.of(
                        Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK,
                Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE)
        )));
    }

}

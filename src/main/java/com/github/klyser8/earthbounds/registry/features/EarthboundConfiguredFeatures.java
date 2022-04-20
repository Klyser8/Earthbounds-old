package com.github.klyser8.earthbounds.registry.features;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeature;
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

    public static ConfiguredFeature<?, ?> OVERWORLD_REDSTONE_FOSSIL =
            new ConfiguredFeature<>(Feature.REPLACE_SINGLE_BLOCK,
            new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                    EarthboundBlocks.REDSTONE_FOSSIL_BLOCK.getDefaultState()));
    public static ConfiguredFeature<?, ?> OVERWORLD_GILDED_REDSTONE_FOSSIL =
            new ConfiguredFeature<>(Feature.REPLACE_SINGLE_BLOCK,
            new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                    EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK.getDefaultState()));
    public static ConfiguredFeature<?, ?> OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL =
            new ConfiguredFeature<>(Feature.REPLACE_SINGLE_BLOCK,
            new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                    EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK.getDefaultState()));
    public static ConfiguredFeature<?, ?> OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL =
            new ConfiguredFeature<>(Feature.REPLACE_SINGLE_BLOCK,
            new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(),
                    EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK.getDefaultState()));

    public static ConfiguredFeature<?, ?> SMALL_COAL_DEN =
            new ConfiguredFeature<>(EarthboundFeatures.COAL_DEN,
            new CoalDenFeatureConfig(
                    ConstantIntProvider.create(-8), ConstantIntProvider.create(12)));
    public static ConfiguredFeature<?, ?> GLOW_GREASE_SPLAT =
            new ConfiguredFeature<>(EarthboundFeatures.GLOW_GREASE_SPLAT,
            new DefaultFeatureConfig());


    //Discard on air chance = Chance that if the block is exposed to air, it is discarded.
    static void register() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Earthbounds.MOD_ID,
                "overworld_redstone_fossil"), OVERWORLD_REDSTONE_FOSSIL);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Earthbounds.MOD_ID,
                "overworld_gilded_redstone_fossil"), OVERWORLD_GILDED_REDSTONE_FOSSIL);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Earthbounds.MOD_ID,
                "overworld_deepslate_redstone_fossil"), OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Earthbounds.MOD_ID,
                        "overworld_deepslate_gilded_redstone_fossil"), OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "small_coal_den"), SMALL_COAL_DEN);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "glow_grease_splat"), GLOW_GREASE_SPLAT);
    }

}

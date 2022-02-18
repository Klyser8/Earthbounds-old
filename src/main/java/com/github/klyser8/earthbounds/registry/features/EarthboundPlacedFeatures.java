package com.github.klyser8.earthbounds.registry.features;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.*;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundPlacedFeatures {

    public static PlacedFeature OVERWORLD_REDSTONE_FOSSIL_CONFIGURED_FEATURE;
    public static PlacedFeature OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL_CONFIGURED_FEATURE;
    public static PlacedFeature OVERWORLD_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE;
    public static PlacedFeature OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE;

    public static PlacedFeature SMALL_COAL_DEN;
    public static PlacedFeature GLOW_GREASE_SPLAT;

    /**
     * {@link RarityFilterPlacementModifier#chance}: indicates the chance that the feature will be placed.
     * The feature will be placed 1 time every [chance] times.
     */
    public void register() {
         SMALL_COAL_DEN = Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "small_coal_den"),
                EarthboundConfiguredFeatures.SMALL_COAL_DEN.withPlacement(
                        RarityFilterPlacementModifier.of(248),
                        SquarePlacementModifier.of(),
                        HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                        EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), 1),
                        BiomePlacementModifier.of()
                ));

         OVERWORLD_REDSTONE_FOSSIL_CONFIGURED_FEATURE = Registry.register(BuiltinRegistries.PLACED_FEATURE,
                 new Identifier(MOD_ID, "overworld_redstone_fossil"),
                 EarthboundConfiguredFeatures.OVERWORLD_REDSTONE_FOSSIL.withPlacement(
                         CountPlacementModifier.of(1),
                         SquarePlacementModifier.of(),
                         RarityFilterPlacementModifier.of(4),
                         HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))));

        OVERWORLD_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE = Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "overworld_gilded_redstone_fossil"),
                EarthboundConfiguredFeatures.OVERWORLD_GILDED_REDSTONE_FOSSIL.withPlacement(
                        CountPlacementModifier.of(1),
                        SquarePlacementModifier.of(),
                        RarityFilterPlacementModifier.of(20),
                        HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))));

         OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL_CONFIGURED_FEATURE = Registry.register(BuiltinRegistries.PLACED_FEATURE,
                 new Identifier(MOD_ID, "overworld_deepslate_redstone_fossil"),
                 EarthboundConfiguredFeatures.OVERWORLD_DEEPSLATE_REDSTONE_FOSSIL.withPlacement(
                         CountPlacementModifier.of(1),
                         SquarePlacementModifier.of(),
                         RarityFilterPlacementModifier.of(4),
                         HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))));

         OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE = Registry.register(BuiltinRegistries.PLACED_FEATURE,
                 new Identifier(MOD_ID, "overworld_deepslate_gilded_redstone_fossil"),
                 EarthboundConfiguredFeatures.OVERWORLD_DEEPSLATE_GILDED_REDSTONE_FOSSIL.withPlacement(
                         CountPlacementModifier.of(1),
                         SquarePlacementModifier.of(),
                         RarityFilterPlacementModifier.of(20),
                         HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(64))));

        GLOW_GREASE_SPLAT = Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(MOD_ID, "glow_grease_splat"),
                EarthboundConfiguredFeatures.GLOW_GREASE_SPLAT.withPlacement(
                        CountPlacementModifier.of(1),
                        PlacedFeatures.BOTTOM_TO_120_RANGE,
                        SquarePlacementModifier.of(),
                        SurfaceThresholdFilterPlacementModifier.of(
                                Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13),
                        RarityFilterPlacementModifier.of(2),
                        BiomePlacementModifier.of()));
    }
}

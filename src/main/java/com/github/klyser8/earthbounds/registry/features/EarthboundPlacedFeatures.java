package com.github.klyser8.earthbounds.registry.features;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundPlacedFeatures {

    public static PlacedFeature REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(2),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));
    public static PlacedFeature GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.GILDED_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(18),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));
    public static PlacedFeature CRYSTALLINE_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.CRYSTALLINE_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));
    public static PlacedFeature CHARRED_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.CHARRED_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));
    public static PlacedFeature VERDANT_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.VERDANT_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));
    public static PlacedFeature CRIMSON_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.CRIMSON_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.aboveBottom(64), YOffset.belowTop(300))
            ));

    public static PlacedFeature DEEPSLATE_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(2),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));
    public static PlacedFeature DEEPSLATE_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_GILDED_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));
    public static PlacedFeature DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));
    public static PlacedFeature DEEPSLATE_CHARRED_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_CHARRED_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));
    public static PlacedFeature DEEPSLATE_VERDANT_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_VERDANT_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));
    public static PlacedFeature DEEPSLATE_CRIMSON_REDSTONE_FOSSIL_CONFIGURED_FEATURE =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.DEEPSLATE_CRIMSON_REDSTONE_FOSSIL), List.of(
                            CountPlacementModifier.of(1),
                            SquarePlacementModifier.of(),
                            RarityFilterPlacementModifier.of(20),
                            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.aboveBottom(64))
            ));

    public static PlacedFeature SMALL_COAL_DEN =
            new PlacedFeature(RegistryEntry.of(
                    EarthboundConfiguredFeatures.SMALL_COAL_DEN), List.of(
                    RarityFilterPlacementModifier.of(192),
                    SquarePlacementModifier.of(),
                    HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG),
                    EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), 1),
                    BiomePlacementModifier.of()
    ));
    public static PlacedFeature GLOW_GREASE_SPLAT =
            new PlacedFeature(RegistryEntry.of(
            EarthboundConfiguredFeatures.GLOW_GREASE_SPLAT), List.of(
                    CountPlacementModifier.of(10),
                    PlacedFeatures.BOTTOM_TO_120_RANGE,
                    SquarePlacementModifier.of(),
                    SurfaceThresholdFilterPlacementModifier.of(
                            Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13),
                    RarityFilterPlacementModifier.of(2),
                    BiomePlacementModifier.of()
    ));

    /**
     * {@link RarityFilterPlacementModifier#chance}: indicates the chance that the feature will be placed.
     * The feature will be placed 1 time every [chance] times.
     */
    static void register() {
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                 new Identifier(MOD_ID, "redstone_fossil"),
                REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "gilded_redstone_fossil"),
                GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "crystalline_redstone_fossil"),
                CRYSTALLINE_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "charred_redstone_fossil"),
                CHARRED_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "verdant_redstone_fossil"),
                VERDANT_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "crimson_redstone_fossil"),
                CRIMSON_REDSTONE_FOSSIL_CONFIGURED_FEATURE);

        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                 new Identifier(MOD_ID, "deepslate_redstone_fossil"),
                DEEPSLATE_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "deepslate_gilded_redstone_fossil"),
                DEEPSLATE_GILDED_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "deepslate_crystalline_redstone_fossil"),
                DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "deepslate_charred_redstone_fossil"),
                DEEPSLATE_CHARRED_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "deepslate_verdant_redstone_fossil"),
                DEEPSLATE_VERDANT_REDSTONE_FOSSIL_CONFIGURED_FEATURE);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "deepslate_crimson_redstone_fossil"),
                DEEPSLATE_CRIMSON_REDSTONE_FOSSIL_CONFIGURED_FEATURE);

        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "glow_grease_splat"), GLOW_GREASE_SPLAT);
        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                new Identifier(MOD_ID, "small_coal_den"), SMALL_COAL_DEN);
    }
}

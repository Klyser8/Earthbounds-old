package com.github.klyser8.earthbounds.registry.features;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeature;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeatureConfig;
import com.github.klyser8.earthbounds.world.features.glowgrease.GlowGreaseFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GlowLichenFeatureConfig;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundFeatures {

    public static final Feature<CoalDenFeatureConfig> COAL_DEN = new CoalDenFeature(CoalDenFeatureConfig.CODEC);
    public static final Feature<GlowLichenFeatureConfig> GLOW_GREASE_SPLAT =
            new GlowGreaseFeature(GlowLichenFeatureConfig.CODEC);

    public static void setupAndRegister() {
        Registry.register(Registry.FEATURE, new Identifier(MOD_ID, "coal_den"), COAL_DEN);
        Registry.register(Registry.FEATURE, new Identifier(MOD_ID, "glow_grease_splat"), GLOW_GREASE_SPLAT);
        EarthboundConfiguredFeatures.register();
        EarthboundPlacedFeatures.register();
        handleBiomeModifications();
    }

    private static void handleBiomeModifications() {
        BiomeModifications.create(new Identifier(MOD_ID, "add_small_coal_den")).add(
                ModificationPhase.ADDITIONS,
                (context) -> {
                    Biome.Category category = context.getBiome().getCategory();
                    return category == Biome.Category.MOUNTAIN || category == Biome.Category.EXTREME_HILLS ||
                            category == Biome.Category.SAVANNA ;
                },
                biomeModificationContext -> biomeModificationContext.getGenerationSettings().
                        addBuiltInFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS,
                                EarthboundPlacedFeatures.SMALL_COAL_DEN));

        BiomeModifications.create(new Identifier(MOD_ID, "add_glow_grease_splat")).add(
                ModificationPhase.ADDITIONS,
                context -> {
                    Biome.Category category = context.getBiome().getCategory();
                    return category != Biome.Category.NETHER && category
                            != Biome.Category.THEEND && category != Biome.Category.NONE;
                },
                context -> context.getGenerationSettings().addBuiltInFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, EarthboundPlacedFeatures.GLOW_GREASE_SPLAT));

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                        new Identifier(MOD_ID, "overworld_redstone_fossil")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                        new Identifier(MOD_ID, "overworld_gilded_redstone_fossil")));

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                        new Identifier(MOD_ID, "overworld_deepslate_redstone_fossil")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                        new Identifier(MOD_ID, "overworld_deepslate_gilded_redstone_fossil")));
    }

}

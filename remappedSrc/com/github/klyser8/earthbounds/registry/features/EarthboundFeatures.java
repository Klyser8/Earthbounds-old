package com.github.klyser8.earthbounds.registry.features;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeature;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeatureConfig;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;

public class EarthboundFeatures {

    public static final Feature<CoalDenFeatureConfig> COAL_DEN = new CoalDenFeature(CoalDenFeatureConfig.CODEC);

    public static void setupAndRegister() {
        EarthboundPlacedFeatures placedFeatures = new EarthboundPlacedFeatures();
        EarthboundConfiguredFeatures configuredFeatures = new EarthboundConfiguredFeatures();
        Registry.register(Registry.FEATURE, new Identifier(Earthbounds.MOD_ID, "coal_den"), COAL_DEN);
        configuredFeatures.register();
        placedFeatures.register();

        BiomeModifications.create(new Identifier(Earthbounds.MOD_ID, "add_small_coal_den")).add(
                ModificationPhase.ADDITIONS,
                (context) -> {
                    Biome.Category category = context.getBiome().getCategory();
                    return category == Biome.Category.MOUNTAIN || category == Biome.Category.EXTREME_HILLS;
                },
                (biomeSelectionContext, biomeModificationContext) -> biomeModificationContext.getGenerationSettings().
                        addBuiltInFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, EarthboundPlacedFeatures.SMALL_COAL_DEN));
    }

}

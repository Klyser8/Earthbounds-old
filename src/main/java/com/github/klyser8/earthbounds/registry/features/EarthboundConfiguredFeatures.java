package com.github.klyser8.earthbounds.registry.features;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.world.features.coalden.CoalDenFeatureConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class EarthboundConfiguredFeatures {

    public static ConfiguredFeature<?, ?> SMALL_COAL_DEN;

    public void register() {
        SMALL_COAL_DEN = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Earthbounds.MOD_ID, "small_coal_den"),
                EarthboundFeatures.COAL_DEN.configure(new CoalDenFeatureConfig(
                        ConstantIntProvider.create(-8), ConstantIntProvider.create(12)
                )));
    }

}

package com.github.klyser8.earthbounds.world.features.coalden;

import com.github.klyser8.earthbounds.Earthbounds;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public record CoalDenFeatureConfig(IntProvider depth, IntProvider diameter) implements FeatureConfig {

    public static final Codec<CoalDenFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("depth").forGetter(CoalDenFeatureConfig::depth),
            IntProvider.VALUE_CODEC.fieldOf("diameter").forGetter(CoalDenFeatureConfig::diameter)
    ).apply(instance, instance.stable(CoalDenFeatureConfig::new)));

}

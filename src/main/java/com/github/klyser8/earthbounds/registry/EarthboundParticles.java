package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.client.particle.*;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EarthboundParticles {

    public static final DefaultParticleType SCORCH_ASH = FabricParticleTypes.simple();
    public static final DefaultParticleType REDSTONE_CRACKLE = FabricParticleTypes.simple();
    public static final DefaultParticleType GREASE_POP = FabricParticleTypes.simple();
    public static final DefaultParticleType GREASE_CHUNK = FabricParticleTypes.simple();
    public static final DefaultParticleType AMETHYST_SHIMMER = FabricParticleTypes.simple();
    public static final DefaultParticleType AMETHYST_CRIT = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "scorch_ash"), SCORCH_ASH);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "crackle"), REDSTONE_CRACKLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "grease_pop"), GREASE_POP);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "grease_chunk"), GREASE_CHUNK);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "amethyst_shimmer"), AMETHYST_SHIMMER);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(Earthbounds.MOD_ID, "amethyst_crit"), AMETHYST_CRIT);
    }

    public static void registerFactories() {
        ParticleFactoryRegistry.getInstance().register(SCORCH_ASH, ScorchAshParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(REDSTONE_CRACKLE, RedstoneCrackleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(GREASE_POP, GreasePopParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(GREASE_CHUNK, GreaseChunkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(AMETHYST_SHIMMER, AmethystShimmerParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(AMETHYST_CRIT, AmethystCritParticle.Factory::new);
    }

}

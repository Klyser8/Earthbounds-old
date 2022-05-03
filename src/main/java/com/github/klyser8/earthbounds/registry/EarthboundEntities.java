package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.entity.misc.*;
import com.github.klyser8.earthbounds.entity.mob.CarboraneaEntity;
import com.github.klyser8.earthbounds.entity.mob.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.entity.renderer.CarboraneaEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.CoalChunkEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.BuckEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.ShimmerShellEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.pertilyo.PertilyoEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.rubro.RubroEntityRenderer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundEntities {

    public static final EntityType<CarboraneaEntity> CARBORANEA = FabricEntityTypeBuilder
            .create(SpawnGroup.CREATURE, CarboraneaEntity::new)
            .dimensions(EntityDimensions.fixed(0.5f, 0.4f))
            .build();

    public static final EntityType<RubroEntity> RUBRO =
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .entityFactory(RubroEntity::new)
                    .dimensions(EntityDimensions.changing(0.85f, 0.8f))
                    .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                            RubroEntity::checkMobSpawn)
                    .build();

    public static final EntityType<PertilyoEntity> PERTILYO =
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .entityFactory(PertilyoEntity::new)
                    .dimensions(EntityDimensions.changing(0.44f, 1.0f))
                    .spawnRestriction(SpawnRestriction.Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                            PertilyoEntity::checkMobSpawn)
                    .build();

    public static final EntityType<CoalChunkEntity> COAL_CHUNK = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "coal_chunk"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, CoalChunkEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f)).build());
    public static final EntityType<ShimmerShellEntity> SHIMMER_SHELL = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "shimmer_shell"),
            FabricEntityTypeBuilder.<ShimmerShellEntity>create(SpawnGroup.MISC, ShimmerShellEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f))
                    .trackRangeBlocks(256).trackedUpdateRate(1).build());
    public static final EntityType<CopperBuckEntity> COPPER_BUCK = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "copper_buck"),
            FabricEntityTypeBuilder.<CopperBuckEntity>create(SpawnGroup.MISC, CopperBuckEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f))
                    .trackRangeBlocks(256).trackedUpdateRate(1).build());
    public static final EntityType<MadderBuckEntity> MADDER_BUCK = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "madder_buck"),
            FabricEntityTypeBuilder.<MadderBuckEntity>create(SpawnGroup.MISC, MadderBuckEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f))
                    .trackRangeBlocks(256).trackedUpdateRate(1).build());
    public static final EntityType<GlowGreaseDropEntity> GLOW_GREASE = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "glow_grease"),
            FabricEntityTypeBuilder.<GlowGreaseDropEntity>create(SpawnGroup.MISC, GlowGreaseDropEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(256).trackedUpdateRate(1).build());
    public static final EntityType<FlingingPotionEntity> FLINGING_POTION = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "flinging_potion"),
            FabricEntityTypeBuilder.<FlingingPotionEntity>create(SpawnGroup.MISC, FlingingPotionEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .trackRangeBlocks(256).trackedUpdateRate(1).build());

    public static void register() {
        Registry.register(Registry.ENTITY_TYPE,
                new Identifier(MOD_ID, "carboranea"), CARBORANEA);
        Registry.register(Registry.ENTITY_TYPE,
                new Identifier(MOD_ID, "rubro"), RUBRO);
        Registry.register(Registry.ENTITY_TYPE,
                new Identifier(MOD_ID, "pertilyo"), PERTILYO);
        createEntityAttributes();

        BiomeModifications.addSpawn(BiomeSelectors.all(),
                SpawnGroup.AMBIENT, EarthboundEntities.RUBRO, 36, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(BiomeKeys.DRIPSTONE_CAVES),
                SpawnGroup.AMBIENT, EarthboundEntities.PERTILYO, 10, 1, 1);
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(CARBORANEA, CarboraneaEntityRenderer::new);
        EntityRendererRegistry.register(RUBRO, RubroEntityRenderer::new);
        EntityRendererRegistry.register(PERTILYO, PertilyoEntityRenderer::new);
        EntityRendererRegistry.register(COAL_CHUNK, CoalChunkEntityRenderer::new);
        EntityRendererRegistry.register(SHIMMER_SHELL, ShimmerShellEntityRenderer::new);
        EntityRendererRegistry.register(COPPER_BUCK, BuckEntityRenderer::new);
        EntityRendererRegistry.register(MADDER_BUCK, BuckEntityRenderer::new);
    }

    @SuppressWarnings("ConstantConditions")
    private static void createEntityAttributes() {
        FabricDefaultAttributeRegistry.register(CARBORANEA, CarboraneaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUBRO, RubroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(PERTILYO, PertilyoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(COAL_CHUNK, CoalChunkEntity.createAttributes());
    }

}

package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.entity.CoalChunkEntity;
import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.entity.renderer.CarboraneaEntityRenderer;
import com.github.klyser8.earthbounds.entity.CarboraneaEntity;
import com.github.klyser8.earthbounds.entity.renderer.CoalChunkEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.PertilyoEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.RubroEntityRenderer;
import com.github.klyser8.earthbounds.mixin.SpawnRestrictionsAccessor;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.HeightmapPlacementModifier;

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
                    .dimensions(EntityDimensions.changing(0.95f, 0.8f))
                    .spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                            RubroEntity::checkMobSpawn)
                    .build();

    public static final EntityType<PertilyoEntity> PERTILYO =
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(SpawnGroup.AMBIENT)
                    .entityFactory(PertilyoEntity::new)
                    .dimensions(EntityDimensions.changing(0.75f, 1.2f))
                    /*.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                            RubroEntity::checkMobSpawn)*/
                    .build();

    public static final EntityType<CoalChunkEntity> COAL_CHUNK = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "coal_chunk"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, CoalChunkEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f)).build());

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
//        registerEntitySpawnRestrictions();
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(CARBORANEA, CarboraneaEntityRenderer::new);
        EntityRendererRegistry.register(RUBRO, RubroEntityRenderer::new);
        EntityRendererRegistry.register(PERTILYO, PertilyoEntityRenderer::new);
        EntityRendererRegistry.register(COAL_CHUNK, CoalChunkEntityRenderer::new);
    }

    @SuppressWarnings("ConstantConditions")
    private static void createEntityAttributes() {
        FabricDefaultAttributeRegistry.register(CARBORANEA, CarboraneaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUBRO, RubroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(PERTILYO, PertilyoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(COAL_CHUNK, CoalChunkEntity.createAttributes());

    }

}

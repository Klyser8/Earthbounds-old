package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.entity.CoalChunkEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.entity.renderer.CarboraneaEntityRenderer;
import com.github.klyser8.earthbounds.entity.CarboraneaEntity;
import com.github.klyser8.earthbounds.entity.renderer.CoalChunkEntityRenderer;
import com.github.klyser8.earthbounds.entity.renderer.RubroEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.github.klyser8.earthbounds.Earthbounds.MOD_ID;

public class EarthboundEntities {

    public static final EntityType<CarboraneaEntity> CARBORANEA = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "carboranea"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, CarboraneaEntity::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.4f)).build());

    public static final EntityType<RubroEntity> RUBRO = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "rubro"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, RubroEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f)).build());

    public static final EntityType<CoalChunkEntity> COAL_CHUNK = Registry.register(Registry.ENTITY_TYPE,
            new Identifier(MOD_ID, "coal_chunk"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, CoalChunkEntity::new)
                    .dimensions(EntityDimensions.fixed(0.2f, 0.2f)).build());

    public static void register() {
        EntityRendererRegistry.register(CARBORANEA, CarboraneaEntityRenderer::new);
        EntityRendererRegistry.register(RUBRO, RubroEntityRenderer::new);
        EntityRendererRegistry.register(COAL_CHUNK, CoalChunkEntityRenderer::new);
        createEntityAttributes();
    }

    private static void createEntityAttributes() {
        FabricDefaultAttributeRegistry.register(CARBORANEA, CarboraneaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(RUBRO, RubroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(COAL_CHUNK, CoalChunkEntity.createAttributes());
    }

}

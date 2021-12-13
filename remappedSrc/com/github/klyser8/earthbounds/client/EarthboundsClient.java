package com.github.klyser8.earthbounds.client;

import com.github.klyser8.earthbounds.Earthbounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class EarthboundsClient implements ClientModInitializer {

    /**
     * Registers our Entity's renderer, which provides a model and texture for the entity.
     *
     * Entity Renderers can also manipulate the model before it renders based on
     * entity context (EndermanEntityRenderer#render).
     */
    @Override
    public void onInitializeClient() {
    }
}

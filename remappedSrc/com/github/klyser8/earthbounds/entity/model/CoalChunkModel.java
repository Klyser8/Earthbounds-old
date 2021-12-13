package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.CoalChunkEntity;
import com.github.klyser8.earthbounds.entity.Earthen;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class CoalChunkModel extends AnimatedGeoModel<CoalChunkEntity> {

    @Override
    public Identifier getModelLocation(CoalChunkEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/misc/coal.geo.json");
    }

    @Override
    public Identifier getTextureLocation(CoalChunkEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/coal.png");
    }

    @Override
    public Identifier getAnimationFileLocation(CoalChunkEntity animatable) {
        return null;
    }

}

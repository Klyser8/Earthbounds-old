package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.CopperBuckEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CopperBuckEntityModel extends AnimatedGeoModel<CopperBuckEntity> {

    @Override
    public Identifier getModelLocation(CopperBuckEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/misc/copper_buck.geo.json");
    }

    @Override
    public Identifier getTextureLocation(CopperBuckEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/copper_buck.png");
    }

    @Override
    public Identifier getAnimationFileLocation(CopperBuckEntity animatable) {
        return new Identifier(Earthbounds.MOD_ID, "animations/earth.copper_buck.json");
    }
}

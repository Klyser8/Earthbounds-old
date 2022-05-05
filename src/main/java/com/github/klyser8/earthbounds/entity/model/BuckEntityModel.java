package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.misc.CopperBuckEntity;
import com.github.klyser8.earthbounds.entity.misc.BuckEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class BuckEntityModel extends AnimatedGeoModel<BuckEntity> {

    @Override
    public Identifier getModelLocation(BuckEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/misc/generic_buck.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BuckEntity buck) {
        if (buck instanceof CopperBuckEntity) {
            return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/copper_buck.png");
        } else {
            return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/madder_buck.png");
        }
    }

    @Override
    public Identifier getAnimationFileLocation(BuckEntity animatable) {
        return new Identifier(Earthbounds.MOD_ID, "animations/earth.generic_buck.json");
    }
}

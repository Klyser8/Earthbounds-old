package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.ShimmerShellEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public class ShimmerShellEntityModel extends AnimatedGeoModel<ShimmerShellEntity> {

    @Override
    public Identifier getModelLocation(ShimmerShellEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/misc/shimmer_shell.geo.json");
    }

    @Override
    public Identifier getTextureLocation(ShimmerShellEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/shimmer_shell.png");
    }

    @Override
    public Identifier getAnimationFileLocation(ShimmerShellEntity animatable) {
        return new Identifier(Earthbounds.MOD_ID, "animations/earth.shimmer_shell.json");
    }
}

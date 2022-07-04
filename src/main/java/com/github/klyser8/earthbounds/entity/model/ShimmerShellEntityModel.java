package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.misc.ShimmerShellEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ShimmerShellEntityModel extends AnimatedGeoModel<ShimmerShellEntity> {

    @Override
    public Identifier getModelResource(ShimmerShellEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/misc/shimmer_shell.geo.json");
    }

    @Override
    public Identifier getTextureResource(ShimmerShellEntity object) {
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/misc/shimmer_shell.png");
    }

    @Override
    public Identifier getAnimationResource(ShimmerShellEntity animatable) {
        return new Identifier(Earthbounds.MOD_ID, "animations/earth.shimmer_shell.json");
    }
}

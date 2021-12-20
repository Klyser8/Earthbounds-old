package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.Earthen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.io.File;

/**
 * Represents the model of a mob, who is an instance of Earthen.
 *
 * @param <T> a class implementing {@link Earthen}
 */
public class EarthboundMobModel<T extends Earthen> extends AnimatedGeoModel<T> {

    protected final String entityKey;

    public EarthboundMobModel(EntityType<? extends Earthen> entity) {
        this.entityKey = entity.getUntranslatedName();
    }

    @Override
    public Identifier getModelLocation(Earthen object) {
        return new Identifier(Earthbounds.MOD_ID, "geo/mob/" + entityKey + ".geo.json");
    }

    @Override
    public Identifier getTextureLocation(Earthen object) {
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/" + entityKey + "/" + entityKey + ".png");
    }

    @Override
    public Identifier getAnimationFileLocation(Earthen animatable) {
        return new Identifier(Earthbounds.MOD_ID, "animations/" + Earthbounds.MOD_ID + "." + entityKey + ".json");
    }
}

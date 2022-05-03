package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.entity.mob.CarboraneaEntity;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class CarboraneaEntityModel extends EarthboundMobModel<CarboraneaEntity> {

    public CarboraneaEntityModel() {
        super(EarthboundEntities.CARBORANEA);
    }

    /**
     * Allows the head to move.
     */
    @Override
    public void setLivingAnimations(CarboraneaEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 300F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 270F));
        }

    }

}

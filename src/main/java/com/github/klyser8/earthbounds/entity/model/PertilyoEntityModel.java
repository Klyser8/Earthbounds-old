package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.mob.PertilyoEntity;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class PertilyoEntityModel extends EarthboundMobModel<PertilyoEntity> {

    public PertilyoEntityModel() {
        super(EarthboundEntities.PERTILYO);
    }

    /**
     * Pertilyo oxidizes further and further every 5 minutes
     */
    @Override
    public Identifier getTextureLocation(PertilyoEntity entity) {
        String suffix = "";
        switch (entity.getOxidizationLevel()) {
            case UNAFFECTED -> suffix = "_0";
            case EXPOSED -> suffix = "_1";
            case WEATHERED -> suffix = "_2";
            case OXIDIZED -> suffix = "_3";
        }
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/" + entityKey + "/" +
                entityKey + suffix + ".png");
    }

    /**
     * Allows the head to move.
     */
    @Override
    public void setLivingAnimations(PertilyoEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 270F));
        }

    }

}

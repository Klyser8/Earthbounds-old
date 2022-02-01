package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.Earthen;
import com.github.klyser8.earthbounds.entity.PertilyoEntity;
import com.github.klyser8.earthbounds.entity.RubroEntity;
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
        if (entity.getSecondsSinceDeox() < 300) {
            suffix = "_0";
        } else if (entity.getSecondsSinceDeox() < 600) {
            suffix = "_1";
        } else if (entity.getSecondsSinceDeox() < 900) {
            suffix = "_2";
        } else {
            suffix = "_3";
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

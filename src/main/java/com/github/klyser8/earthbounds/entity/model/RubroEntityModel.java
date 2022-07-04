package com.github.klyser8.earthbounds.entity.model;

import com.github.klyser8.earthbounds.Earthbounds;
import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class RubroEntityModel extends EarthboundMobModel<RubroEntity> {

    public RubroEntityModel() {
        super(EarthboundEntities.RUBRO);
    }

    /**
     * Rubros may have a deepslate variant.
     */
    @Override
    public Identifier getTextureResource(RubroEntity entity) {
        String prefix = "";
        if (entity.isDeepslate()) {
            prefix = "deepslate_";
        }
        return new Identifier(Earthbounds.MOD_ID, "textures/entity/" + entityKey + "/" +
                prefix + entityKey + ".png");
    }

    /**
     * Allows the head to move.
     */
    @Override
    public void setLivingAnimations(RubroEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isActive()) {
            return;
        }
        IBone head = this.getAnimationProcessor().getBone("head");
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        if (head != null) {
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 150F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 270F));
        }

    }

}

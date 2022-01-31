package com.github.klyser8.earthbounds.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class PertilyoEntity extends PathAwareEarthenEntity implements Earthen {

    public PertilyoEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return DefaultAttributeContainer.builder()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.25D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 10.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_ARMOR, 15)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this,
                "move", 10, this::movementPredicate));
    }

    private <E extends IAnimatable> PlayState movementPredicate(AnimationEvent<E> event) {
//        if (event.isMoving()) {
            event.getController().transitionLengthTicks = 3;
            event.getController().setAnimation(
                        new AnimationBuilder().addAnimation("flap_loop", true));
            return PlayState.CONTINUE;
//        }
//        return PlayState.STOP;
    }
}

package com.github.klyser8.earthbounds.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class PertilyoEntity extends PathAwareEarthenEntity implements Earthen {

    private static final TrackedData<Integer> ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> MAX_ENERGY = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SECONDS_SINCE_DEOX = DataTracker.registerData(PertilyoEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

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
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(ENERGY, 0);
        dataTracker.startTracking(MAX_ENERGY, 10);
        dataTracker.startTracking(SECONDS_SINCE_DEOX, 0);
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

    @Override
    public void tick() {
        super.tick();
        if (age % 20 == 0) {
            setSecondsSinceDeox(getSecondsSinceDeox() + 1);
        }
    }

    public int getEnergy() {
        return dataTracker.get(ENERGY);
    }

    public void setEnergy(int energy) {
        dataTracker.set(ENERGY, Math.max(0, energy));
    }

    public int getMaxEnergy() {
        return dataTracker.get(MAX_ENERGY);
    }

    /**
     * Seconds since last de-oxidation happened. This can only happen through being struck by lightning.
     */
    public int getSecondsSinceDeox() {
        return dataTracker.get(SECONDS_SINCE_DEOX);
    }

    public void setSecondsSinceDeox(int time) {
        dataTracker.set(SECONDS_SINCE_DEOX, time);
    }
}

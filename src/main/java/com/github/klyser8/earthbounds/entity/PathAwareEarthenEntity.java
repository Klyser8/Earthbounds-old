package com.github.klyser8.earthbounds.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class PathAwareEarthenEntity extends PathAwareEntity implements Earthen {

    private final AnimationFactory factory;

    private static final TrackedData<Integer> LAST_DAMAGER_ID = DataTracker.registerData(RubroEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    protected PathAwareEarthenEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.factory = new AnimationFactory(this);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(LAST_DAMAGER_ID, getId());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource)
                || damageSource.equals(DamageSource.ON_FIRE)
                || damageSource.equals(DamageSource.IN_FIRE);
    }

    @Override
    public EntityGroup getGroup() {
        return EarthboundEntityGroup.EARTHEN;
    }

    @Override
    public Entity getLastDamager() {
        return world.getEntityById(dataTracker.get(LAST_DAMAGER_ID));
    }

    @Override
    public void setLastDamager(Entity entity) {
        dataTracker.set(LAST_DAMAGER_ID, entity.getId());
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }


}

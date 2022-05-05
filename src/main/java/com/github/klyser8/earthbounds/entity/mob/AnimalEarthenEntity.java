package com.github.klyser8.earthbounds.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class AnimalEarthenEntity extends AnimalEntity implements Earthen {

    private final AnimationFactory factory;

    private static final TrackedData<Integer> LAST_DAMAGER_ID = DataTracker.registerData(AnimalEarthenEntity.class,
            TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> LAST_DAMAGE_SOURCE_NAME = DataTracker.registerData(AnimalEarthenEntity.class,
            TrackedDataHandlerRegistry.STRING);
    //Holds ID of the current animation flag.
    private static final TrackedData<Byte> ANIMATION_STATE = DataTracker.registerData(AnimalEarthenEntity.class,
            TrackedDataHandlerRegistry.BYTE);

    //Default state for any entity
    protected static final byte DEFAULT_STATE = 0;

    protected AnimalEarthenEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
        this.factory = new AnimationFactory(this);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(LAST_DAMAGER_ID, getId());
        dataTracker.startTracking(ANIMATION_STATE, (byte) 0);
        dataTracker.startTracking(LAST_DAMAGE_SOURCE_NAME, "");
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
        return dataTracker.get(LAST_DAMAGER_ID) == null ? null : world.getEntityById(dataTracker.get(LAST_DAMAGER_ID));
    }

    @Override
    public void setLastDamager(@Nullable Entity entity) {
        dataTracker.set(LAST_DAMAGER_ID, entity != null ? entity.getId() : null);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source == null) {
            return super.damage(source, amount);
        }
        if (!world.isClient) {
            setLastDamageSourceName(source.getName());
        }
        if (getLastDamager() == null || (source.getSource() != null && !getLastDamager().equals(source.getSource()))) {
            setLastDamager(source.getSource());
        }
        return super.damage(source, amount);
    }

    public int getAnimationState() {
        return dataTracker.get(ANIMATION_STATE);
    }

    public void setAnimationState(byte id) {
        dataTracker.set(ANIMATION_STATE, id);
    }

    @Override
    public String getLastDamageSourceName() {
        return dataTracker.get(LAST_DAMAGE_SOURCE_NAME);
    }

    @Override
    public void setLastDamageSourceName(String name) {
        dataTracker.set(LAST_DAMAGE_SOURCE_NAME, name);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}

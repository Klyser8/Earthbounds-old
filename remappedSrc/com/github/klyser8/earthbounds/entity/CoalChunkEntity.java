package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.mixin.AbstractFurnaceBlockEntityAccessor;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

/**
 * Represents a coal chunk.
 * Eventually should be changed to an Entity and not a MobEntity as it's not correct.
 * Should also not implement IAnimatable.
 */
public class CoalChunkEntity extends MobEntity implements Conductive, IAnimatable {

    private AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<Float> HEAT = DataTracker.registerData(CarboraneaEntity.class,
            TrackedDataHandlerRegistry.FLOAT);
    public static final int MAX_AGE = 1200;
    private final float scale;
    private boolean extinguished;

    public CoalChunkEntity(EntityType<? extends MobEntity> type, World world/*, float heat*/) {
        super(type, world);
        dataTracker.set(HEAT, MAX_HEAT - random.nextFloat());
        scale = 1.5f - random.nextFloat();
        extinguished = false;
    }

    @Override
    public float updateHeat(int period) {
        float currentHeat = dataTracker.get(HEAT);
        float heatChange = Conductive.calculateHeatChangePerPeriod(world, getEyePos(), false);
        currentHeat += heatChange;
        if (currentHeat > MAX_HEAT) {
            currentHeat = MAX_HEAT;
        } else if (currentHeat < 0) {
            currentHeat = 0;
        }
        if (currentHeat == 0) {
            extinguished = true;
        }
        dataTracker.set(HEAT, currentHeat);
        return heatChange;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.changing(scale / 7, scale / 7);
    }

    @Override
    public void tick() {
        super.tick();
        int period = 10;
        if (age % period == 0) {
            updateHeat(period);
        }
        if (extinguished || age == MAX_AGE) {
            remove(RemovalReason.KILLED);
            return;
        }
        if (getCurrentHeat() > MAX_HEAT / 8) {
            List<Entity> entities = world.getOtherEntities(this, calculateBoundingBox());
            for (Entity entity : entities) {
                if (entity.isFireImmune() || entity instanceof CoalChunkEntity) continue;
                entity.setFireTicks(20);
                entity.damage(DamageSource.ON_FIRE, getCurrentHeat());
            }
        }
        for (double x = -0.5; x < 1.5; x++) {
            for (double y = -0.5; y < 1.5; y++) {
                for (double z = -0.5; z < 1.5; z++) {
                    BlockEntity block = world.getBlockEntity(new BlockPos(getX() + x, getY() + y, getZ() + z));
                    if (block instanceof AbstractFurnaceBlockEntity furnace) {
                        int burnTime = ((AbstractFurnaceBlockEntityAccessor) furnace).getBurnTime();
                        ((AbstractFurnaceBlockEntityAccessor) furnace)
                                .setBurnTime(burnTime + (int) (getCurrentHeat() * 10 * scale));
                        remove(RemovalReason.KILLED);
                    }
                }
            }
        }
    }

    @Override
    public boolean isInvulnerable() {
        return false;
    }

    /**
     * Coal chunks are invulnerable unless they fall out of the world or are killed via commands.
     */
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOutOfWorld() || source.isMagic()) {
            return super.damage(source, amount);
        } else {
            return false;
        }
    }

    @Override
    public float getCurrentHeat() {
        return dataTracker.get(HEAT);
    }

    @Override
    public void setCurrentHeat(float heat) {
        dataTracker.set(HEAT, heat);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(HEAT, 0.0f);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean collides() {
        return age > 20;
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    /**
     * Gets the scale of the coal chunk. Depends on randomness
     */
    public float getScale() {
        return scale;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PassiveEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

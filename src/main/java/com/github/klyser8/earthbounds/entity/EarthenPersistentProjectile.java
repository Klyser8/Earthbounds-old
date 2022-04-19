package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public abstract class EarthenPersistentProjectile extends PersistentProjectileEntity implements IAnimatable {

    private final AnimationFactory factory;
    private static final TrackedData<Integer> collisionAge = DataTracker.registerData(ShimmerShellEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public EarthenPersistentProjectile(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
        pickupType = PickupPermission.ALLOWED;
    }

    public EarthenPersistentProjectile(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
                              World world, LivingEntity owner) {
        super(type, x, y, z, world);
        this.factory = new AnimationFactory(this);
        setOwner(owner);
        pickupType = PickupPermission.ALLOWED;
    }

    protected EarthenPersistentProjectile(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
        this.factory = new AnimationFactory(this);
        setOwner(owner);
        pickupType = PickupPermission.ALLOWED;
    }

    @Override
    public void tick() {
        if (getCollisionAge() == 0 && isInsideWall()) {
            setCollisionAge(1);
        }
        if (getCollisionAge() > 0) {
            setCollisionAge(getCollisionAge() + 1);
        }
        super.tick();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType() != HitResult.Type.MISS) {
            if (getCollisionAge() == 0) {
                setCollisionAge(1);
            }
        }
    }

    public int getCollisionAge() {
        return dataTracker.get(collisionAge);
    }

    public void setCollisionAge(int age) {
        dataTracker.set(collisionAge, age);
    }

    @Override
    public void registerControllers(AnimationData data) {}

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        dataTracker.startTracking(collisionAge, 0);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    protected ItemStack asItemStack() {
        return EarthboundItems.COPPER_BUCK.getDefaultStack();
    }
}

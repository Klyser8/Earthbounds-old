package com.github.klyser8.earthbounds.entity.misc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;

public abstract class BuckEntity extends EarthenPersistentProjectile implements IAnimatable {


    public BuckEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        pickupType = PickupPermission.DISALLOWED;
    }

    public BuckEntity(EntityType<? extends PersistentProjectileEntity> type,
                      double x, double y, double z, World world, LivingEntity owner) {
        super(type, x, y, z, world, owner);
        pickupType = PickupPermission.DISALLOWED;
    }

    protected BuckEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
        pickupType = PickupPermission.DISALLOWED;
    }

    protected abstract void playParticleTrail();

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            pickupType = PickupPermission.CREATIVE_ONLY;
        } else {
            if (pickupType == PickupPermission.DISALLOWED) {
                pickupType = PickupPermission.ALLOWED;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (getCollisionAge() > 40 && pickupType == PickupPermission.CREATIVE_ONLY) {
            discard();
        }
    }
}

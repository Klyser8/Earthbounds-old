package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundDamageSource;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.mojang.datafixers.DataFixer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CopperBuckEntity extends EarthenPersistentProjectile{

    public CopperBuckEntity(EntityType<CopperBuckEntity> type, World world) {
        super(type, world);
        pickupType = PickupPermission.CREATIVE_ONLY;
    }

    public CopperBuckEntity(double x, double y, double z,
                            World world, LivingEntity owner) {
        super(EarthboundEntities.COPPER_BUCK, x, y, z, world, owner);
        pickupType = PickupPermission.CREATIVE_ONLY;
    }

    protected CopperBuckEntity(LivingEntity owner, World world) {
        super(EarthboundEntities.COPPER_BUCK, owner, world);
        pickupType = PickupPermission.CREATIVE_ONLY;
    }

    @Override
    public double getDamage() {
        return 5 + random.nextDouble() * 3.0;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        setVelocity(getVelocity().negate().multiply(0.1));
        Entity entityHit = entityHitResult.getEntity();
        entityHit.damage(EarthboundDamageSource.copperBuck(this, getOwner()), (float) getDamage());
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT;
    }

    @Override
    public void tick() {
        super.tick();
        playParticleTrail();
    }

    private void playParticleTrail() {
        Vec3d vel = getVelocity().normalize();
        if (world.isClient && getCollisionAge() == 0) {
            for (int i = 0; i < 3; ++i) {
                Vec3d origin = new Vec3d( //subtracting vel.x/y/z will have the trail start more forward/back
                        getParticleX(0.5) - vel.x,
                        getRandomBodyY() / 2 + getRandomBodyY() / 2 - vel.y,
                        getParticleZ(0.5) - vel.z);
                this.world.addParticle(ParticleTypes.CRIT,
                        origin.x + vel.x * i / 4.0,
                        origin.y + vel.y * i / 8.0,
                        origin.z + vel.z * i / 4.0,
                        -vel.x / 2, (-vel.y + 0.2) / 2, -vel.z / 2);
            }
        }
    }

}

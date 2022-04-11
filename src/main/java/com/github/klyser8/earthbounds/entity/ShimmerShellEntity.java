package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundsAdvancementCriteria;
import com.github.klyser8.earthbounds.registry.ShimmerDamageSource;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import software.bernie.example.ClientListener;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

public class ShimmerShellEntity extends PersistentProjectileEntity implements IAnimatable {

    private final AnimationFactory factory;
    private static final TrackedData<Integer> collisionAge = DataTracker.registerData(ShimmerShellEntity.class,
            TrackedDataHandlerRegistry.INTEGER);

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
        pickupType = PickupPermission.DISALLOWED;
    }

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
                              World world, LivingEntity owner) {
        super(type, x, y, z, world);
        this.factory = new AnimationFactory(this);
        setOwner(owner);
        pickupType = PickupPermission.DISALLOWED;
    }

    protected ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world) {
        super(type, owner, world);
        this.factory = new AnimationFactory(this);
        setOwner(owner);
        pickupType = PickupPermission.DISALLOWED;
    }

    @Override
    protected ItemStack asItemStack() {
        return EarthboundItems.SHIMMER_SHELL.getDefaultStack();
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
    public Packet<?> createSpawnPacket() {
        return super.createSpawnPacket();
    }

    @Override
    public double getDamage() {
        return 15;
    }

    @Override
    public void tick() {
        if (getCollisionAge() == 0 && isInsideWall()) {
            setCollisionAge(1);
        }
        playParticleTrail();
        if (getCollisionAge() > 0) {
            setCollisionAge(getCollisionAge() + 1);
        }
        if (getCollisionAge() >= 11) {
            if (!world.isClient) {
                explode();
            }
        }
        super.tick();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entityHit = entityHitResult.getEntity();
        setVelocity(getVelocity().negate().multiply(0.1));
        entityHitResult.getEntity().damage(ShimmerDamageSource.shell(this, getOwner()),
                (float) getDamage() / 2);
        if (entityHit instanceof LivingEntity living) {
            attemptAdvancementTrigger(living);
        }
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

    private void explode() {
        playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 2.0f);
        List<Entity> entities = world.getOtherEntities(this, Box.of(getPos(), 4, 4, 4),
                entity -> entity instanceof LivingEntity);
        for (Entity entity : entities) {
            LivingEntity living = (LivingEntity) entity;
            float dmg = (float) (getDamage() / distanceTo(entity));
            living.damage(ShimmerDamageSource.shimmerExplosion(
                    (LivingEntity) getOwner()), (float) Math.min(dmg, getDamage()));
            attemptAdvancementTrigger(living);
        }
        if (!world.isClient) {
            ((ServerWorld) world).spawnParticles(EarthboundParticles.AMETHYST_SHIMMER,
                    getX(), getY(), getZ(), 50, 0, 0, 0, 0.1);
            DustColorTransitionParticleEffect effect = new DustColorTransitionParticleEffect(
                    new Vec3f(201 / 255f, 94 / 255f, 1),
                    new Vec3f(0.8f, 0.8f, 0.8f), 1.5f);
            ((ServerWorld) world).spawnParticles(effect,
                    getX(), getY() + 0.5, getZ(), 25, 0.5, 0.5, 0.5, 0);
            ((ServerWorld) world).spawnParticles(ParticleTypes.POOF,
                    getX(), getY(), getZ(), 25, 0, 0, 0, 0.1);
            discard();
        }
    }

    private void attemptAdvancementTrigger(LivingEntity entity) {
        if (entity.getHealth() <= 0) {
            if (getOwner() instanceof ServerPlayerEntity player) {
                EarthboundsAdvancementCriteria.KILLED_BY_SHIMMER_SHELL.trigger(player, entity);
            }
        }
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT;
    }

    public int getCollisionAge() {
        return dataTracker.get(collisionAge);
    }

    public void setCollisionAge(int age) {
        dataTracker.set(collisionAge, age);
    }

    private void playParticleTrail() {
        Vec3d vel = getVelocity().normalize();
        if (world.isClient && getCollisionAge() == 0) {
            for (int i = 0; i < 4; ++i) {
                Vec3d origin = new Vec3d(
                        getParticleX(0.5) - vel.x / 2,
                        getRandomBodyY() / 2 + getRandomBodyY() / 2 - vel.y / 2,
                        getParticleZ(0.5) - vel.z / 2);
                this.world.addParticle(EarthboundParticles.AMETHYST_CRIT,
                        origin.x + vel.x * i / 4.0,
                        origin.y + vel.y * i / 8.0,
                        origin.z + vel.z * i / 4.0,
                        -vel.x / 2, (-vel.y + 0.2) / 2, -vel.z / 2);
            }
        }
    }
}

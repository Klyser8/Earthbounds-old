package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import software.bernie.example.ClientListener;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.List;

public class ShimmerShellEntity extends PersistentProjectileEntity implements IAnimatable {

    private final AnimationFactory factory;
    private int collisionAge = 0;

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
    }

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
                              World world, LivingEntity owner) {
        super(type, x, y, z, world);
        this.factory = new AnimationFactory(this);
        setOwner(owner);
    }

    @Override
    protected ItemStack asItemStack() {
        return null;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller",
                0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("spin", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return ClientListener.EntityPacket.createPacket(this);
    }

    @Override
    public double getDamage() {
        return 10;
    }

    @Override
    public void tick() {
        if (getVelocity().length() < 0.1 && isOnGround()) {
            setVelocity(0, 0, 0);
        }
        System.out.println("Collision:" + collisionAge);
        System.out.println("age:" + age);
        if (!EarthUtil.isOnCooldown(age, collisionAge, 10) && collisionAge != 0) {
            discard();
            playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 2.0f);
        }
        super.tick();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        if (collisionAge == 0) {
            collisionAge = age;
            System.out.println(collisionAge);
            explode(); //FIX error when exploding ig uess
        }
    }

    private void explode() {
        List<Entity> entities = world.getOtherEntities(this, Box.of(getPos(), 2, 2, 2),
                entity -> entity instanceof LivingEntity);
        for (Entity entity : entities) {
            LivingEntity living = (LivingEntity) entity;
            float dmg = (float) (getDamage() / distanceTo(entity));
            living.damage(DamageSource.thrownProjectile(this, getOwner()), dmg);
        }
    }
}

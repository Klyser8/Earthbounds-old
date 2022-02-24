package com.github.klyser8.earthbounds.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.example.ClientListener;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ShimmerShellEntity extends PersistentProjectileEntity implements IAnimatable {

    private final AnimationFactory factory;

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        factory = new AnimationFactory(this);
    }

    public ShimmerShellEntity(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
                              World world) {
        super(type, x, y, z, world);
        this.factory = new AnimationFactory(this);
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
    public void tick() {
        System.out.println(getVelocity().length());
        if (getVelocity().length() < 0.1 && isOnGround()) {
            setVelocity(0, 0, 0);
        }
        super.tick();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        setVelocity(getVelocity().x * 0.9, -getVelocity().y * 0.9 - 0.1, getVelocity().z * 0.9);
    }
}

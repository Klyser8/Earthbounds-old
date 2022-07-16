package com.github.klyser8.earthbounds.entity.misc;

import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.registry.EarthboundSounds;
import com.github.klyser8.earthbounds.registry.EarthboundStatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MadderBuckEntity extends BuckEntity {

    public MadderBuckEntity(EntityType<MadderBuckEntity> type, World world) {
        super(type, world);
    }

    public MadderBuckEntity(double x, double y, double z,
                            World world, LivingEntity owner) {
        super(EarthboundEntities.MADDER_BUCK, x, y, z, world, owner);
    }

    protected MadderBuckEntity(LivingEntity owner, World world) {
        super(EarthboundEntities.MADDER_BUCK, owner, world);
    }

    @Override
    public double getDamage() {
        return 0;
    }

    @Override
    protected SoundEvent getHitSound() {
        return EarthboundSounds.MADDER_BUCK_LAND;
    }

    @Override
    public void tick() {
        super.tick();
        playParticleTrail();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        //madder bucks were supposed to power blocks hit. Undoable.
        /*if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = world.getBlockState(((BlockHitResult) hitResult).getBlockPos());
            if (blockState.getProperties().contains(Properties.POWERED)) {
            }
        }*/
        if (hitResult.getType() == HitResult.Type.ENTITY
                && ((EntityHitResult) hitResult).getEntity() instanceof LivingEntity entity) {
            explode(entity);
        } else {
            explode(null);
        }
    }

    @Override
    protected void playParticleTrail() {
        if (world.isClient && getCollisionAge() == 0) {
            Vec3d vel = getVelocity().normalize();
            for (int i = 0; i < 3; ++i) {
                Vec3d origin = new Vec3d( //subtracting vel.x/y/z will have the trail start more forward/back
                        getParticleX(0.5) - vel.x,
                        getRandomBodyY() / 2 + getRandomBodyY() / 2 - vel.y,
                        getParticleZ(0.5) - vel.z);
                this.world.addParticle(DustParticleEffect.DEFAULT,
                        origin.x + vel.x * i / 4.0,
                        origin.y + vel.y * i / 8.0,
                        origin.z + vel.z * i / 4.0,
                        -vel.x / 2, (-vel.y + 0.2) / 2, -vel.z / 2);
            }
        }
    }

    private void explode(@Nullable LivingEntity hitEntity) {
        if (world.isClient) {
            return;
        }
        Box box = this.getBoundingBox().expand(3.0, 2.0, 3.0);
        List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!list.isEmpty()) {
            Entity effectSourceEntity = getEffectCause();
            for (LivingEntity entity : list) {
                double sqDistance = squaredDistanceTo(entity);
                double durationMultiplier = 1.0 - Math.sqrt(sqDistance) / 3.0;
                if (entity == hitEntity) {
                    durationMultiplier = 1.0;
                }
                int duration = (int) (durationMultiplier * 1800 + 0.5);
                if (duration <= 20) continue;
                StatusEffectInstance statusEffect = new StatusEffectInstance(EarthboundStatusEffects.RUBIA, duration, 0);
                if (!entity.canHaveStatusEffect(statusEffect)) {
                    continue;
                }
                entity.addStatusEffect(statusEffect, effectSourceEntity);
                for (int i = 0; i < 20; i++) {
                    ((ServerWorld) world).spawnParticles(EarthboundParticles.REDSTONE_CRACKLE,
                            entity.getParticleX(0.5), entity.getRandomBodyY(),
                            entity.getParticleZ(0.5), 0, 0, 0, 0, 0);
                }
            }
        }
        DustColorTransitionParticleEffect dust = new DustColorTransitionParticleEffect(
                new Vec3f(1, 0, 0),
                new Vec3f(1, 1, 1), 1f);
        ((ServerWorld) world).spawnParticles(dust,
            getX(), getY() + 0.5, getZ(), 20, 0.5, 0.3, 0.5, 0.1);
        ((ServerWorld) world).spawnParticles(EarthboundParticles.RUBRO_MADDER,
                getX(), getY(), getZ(), 50, 0.1, 0.0, 0.1, 0.15);
        discard();
    }

}

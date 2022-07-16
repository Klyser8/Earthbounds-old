package com.github.klyser8.earthbounds.entity.misc;

import com.github.klyser8.earthbounds.item.EarthboundItem;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import com.github.klyser8.earthbounds.registry.EarthboundStatusEffects;
import com.github.klyser8.earthbounds.statuseffect.RubiaStatusEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.List;

public class BottledRubiaEntity extends ThrownItemEntity {

    public BottledRubiaEntity(EntityType<? extends BottledRubiaEntity> entityType, World world) {
        super(entityType, world);
    }

    public BottledRubiaEntity(World world, LivingEntity owner) {
        super(EarthboundEntities.BOTTLED_RUBIA, owner, world);
    }

    public BottledRubiaEntity(World world, double x, double y, double z) {
        super(EarthboundEntities.BOTTLED_RUBIA, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return EarthboundItems.BOTTLED_RUBIA;
    }

    @Override
    protected float getGravity() {
        return 0.07f;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (this.world instanceof ServerWorld) {
            this.world.syncWorldEvent(WorldEvents.SPLASH_POTION_SPLASHED, this.getBlockPos(), PotionUtil.getColor(Potions.HEALING));
            this.discard();
        }
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
                getBoundingBox().expand(4, 2, 4), LivingEntity::isAlive);
        for (LivingEntity entity : entities) {
            double distance = squaredDistanceTo(entity);
            int duration = (int) (1200 * (1.0 - Math.sqrt(distance) / 4.0));
            if (duration < 20) {
                return;
            }
            StatusEffectInstance statusEffect = new StatusEffectInstance(EarthboundStatusEffects.RUBIA, duration, 0);
            entity.addStatusEffect(statusEffect);
            if (!world.isClient) {
                for (int i = 0; i < 20; i++) {
                    ((ServerWorld) world).spawnParticles(EarthboundParticles.REDSTONE_CRACKLE,
                            entity.getParticleX(0.5), entity.getRandomBodyY(),
                            entity.getParticleZ(0.5), 0, 0, 0, 0, 0);
                }
            }
        }
    }

}

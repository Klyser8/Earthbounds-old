package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.client.EarthboundsClient;
import com.github.klyser8.earthbounds.item.flingshot.FlingingPotionItem;
import com.github.klyser8.earthbounds.network.EntitySpawnPacket;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import com.github.klyser8.earthbounds.registry.EarthboundParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class FlingingPotionEntity extends PotionEntity {

    public FlingingPotionEntity(EntityType<? extends FlingingPotionEntity> entityType, World world) {
        super(entityType, world);
    }

    public FlingingPotionEntity(World world, LivingEntity owner) {
        super(world, owner);
    }

    public FlingingPotionEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return EarthboundItems.FLINGING_POTION;
    }

    @Override
    public void tick() {
        super.tick();
        for (int i = 0; i < 2; ++i) {
            ((ServerWorld) world).spawnParticles(ParticleTypes.INSTANT_EFFECT, getX(),
                    getY(), getZ(), 1, 0, 0, 0, 0);
        }
    }
}

package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.client.EarthboundsClient;
import com.github.klyser8.earthbounds.network.EntitySpawnPacket;
import com.github.klyser8.earthbounds.registry.EarthboundItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
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

}

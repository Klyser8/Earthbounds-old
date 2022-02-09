package com.github.klyser8.earthbounds.item.flingshot;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Represents an item which can be flung using {@link FlingshotItem}
 */
public interface Flingable {

    ThrownItemEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter);

}

package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.entity.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.entity.ShimmerShellEntity;
import com.github.klyser8.earthbounds.item.flingshot.Flingable;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShimmerShellItem extends Item implements Flingable {

    public ShimmerShellItem(Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        return new ShimmerShellEntity(EarthboundEntities.SHIMMER_SHELL, eyePos.x, eyePos.y, eyePos.z, world);
    }

}

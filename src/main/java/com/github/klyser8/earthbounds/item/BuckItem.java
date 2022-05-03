package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.entity.misc.CopperBuckEntity;
import com.github.klyser8.earthbounds.entity.misc.MadderBuckEntity;
import com.github.klyser8.earthbounds.entity.misc.BuckEntity;
import com.github.klyser8.earthbounds.item.flingshot.Flingable;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BuckItem extends Item implements Flingable {

    protected final EntityType<? extends BuckEntity> buckType;

    public BuckItem(Settings settings, EntityType<? extends BuckEntity> buckType) {
        super(settings);
        this.buckType = buckType;
    }

    @Override
    public ProjectileEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        Vec3d dir = shooter.getRotationVec(1).normalize();
        Vec3d spawnPos = new Vec3d(eyePos.x + dir.x * 0.2,
                eyePos.y + dir.y * 0.2,
                eyePos.z + dir.z * 0.2);
        if (buckType.equals(EarthboundEntities.COPPER_BUCK)) {
            return new CopperBuckEntity(spawnPos.x, spawnPos.y, spawnPos.z, world, shooter);
        } else if (buckType.equals(EarthboundEntities.MADDER_BUCK)) {
            return new MadderBuckEntity(spawnPos.x, spawnPos.y, spawnPos.z, world, shooter);
        }
        return null;
    }
}

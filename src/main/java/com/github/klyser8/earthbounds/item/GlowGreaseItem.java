package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.entity.misc.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.item.flingshot.Flingable;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GlowGreaseItem extends AliasedBlockItem implements Flingable {

    public GlowGreaseItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ProjectileEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        return new GlowGreaseDropEntity(world, stack, shooter,
                eyePos.x, eyePos.y, eyePos.z, true);
    }
}

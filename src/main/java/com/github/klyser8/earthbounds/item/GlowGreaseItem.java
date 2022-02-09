package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.entity.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.item.flingshot.Flingable;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GlowGreaseItem extends AliasedBlockItem implements Flingable {

    public GlowGreaseItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        /*ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {

            GlowGreaseDropEntity greaseDrop = new GlowGreaseDropEntity(world, stack, user,
                    user.getEyePos().x, user.getEyePos().y, user.getEyePos().z, false);
            world.spawnEntity(greaseDrop);
            greaseDrop.setVelocity(user, user.getPitch(), user.getYaw(), 1.0f, 1.0f, 1.0f);
        }
        return TypedActionResult.success(stack, true);*/
        return super.use(world, user, hand);
    }

    @Override
    public ThrownItemEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        return new GlowGreaseDropEntity(world, stack, shooter,
                eyePos.x, eyePos.y, eyePos.z, true);
    }
}

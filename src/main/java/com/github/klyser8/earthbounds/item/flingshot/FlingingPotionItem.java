package com.github.klyser8.earthbounds.item.flingshot;

import com.github.klyser8.earthbounds.entity.FlingingPotionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlingingPotionItem extends PotionItem implements Flingable {

    public FlingingPotionItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public ThrownItemEntity createFlingableEntity(World world, ItemStack stack, LivingEntity shooter) {
        Vec3d eyePos = shooter.getEyePos();
        FlingingPotionEntity potionEntity = new FlingingPotionEntity(shooter, world, eyePos.x, eyePos.y, eyePos.z);
        potionEntity.setItem(stack);
        return potionEntity;
    }
}

package com.github.klyser8.earthbounds.item;

import com.github.klyser8.earthbounds.entity.GlowGreaseDropEntity;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GlowGreaseItem extends AliasedBlockItem {

    public GlowGreaseItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient) {

            GlowGreaseDropEntity greaseDrop = new GlowGreaseDropEntity(world, user,
                    user.getEyePos().x, user.getEyePos().y, user.getEyePos().z, stack);
//            GlowGreaseDropEntity greaseDrop = EarthboundEntities.GLOW_GREASE.create(world);
//            greaseDrop.setPos(user.getX(), user.getY() + 3, user.getZ());
            world.spawnEntity(greaseDrop);
        }
        return TypedActionResult.success(stack, true);
    }
}

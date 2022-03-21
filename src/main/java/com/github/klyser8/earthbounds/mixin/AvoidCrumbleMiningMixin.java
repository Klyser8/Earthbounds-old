package com.github.klyser8.earthbounds.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public abstract class AvoidCrumbleMiningMixin extends MiningToolItem {

    protected AvoidCrumbleMiningMixin(float attackDamage, float attackSpeed, ToolMaterial material,
                                      TagKey<Block> effectiveBlocks, Settings settings) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        NbtList list = miner.getMainHandStack().getEnchantments();
        if (list.isEmpty()) return true;
        for (NbtElement element : list) {
            if (element.toString().contains("crumble") && miner.isCreative()) return false;
        }
        return true;
    }
}

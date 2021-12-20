package com.github.klyser8.earthbounds.block;

import com.github.klyser8.earthbounds.entity.RubroEntity;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RedstoneFossilBlock extends RedstoneOreBlock {

    public RedstoneFossilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            boolean goldSkull = state.getBlock().equals(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK) ||
                    state.getBlock().equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
            boolean deepslate = state.getBlock().equals(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK) ||
                    state.getBlock().equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
            RubroEntity rubro = EarthboundEntities.RUBRO.create(world);
            if (rubro == null) return;
            rubro.refreshPositionAndAngles((double)pos.getX() + 0.5D, pos.getY(),
                    (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(rubro);
            rubro.initializeFossil(deepslate, true, goldSkull, -200 - world.random.nextInt(192));
        }
    }
}

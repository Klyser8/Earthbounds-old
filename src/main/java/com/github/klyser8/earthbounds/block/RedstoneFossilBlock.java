package com.github.klyser8.earthbounds.block;

import com.github.klyser8.earthbounds.entity.mob.RubroEntity;
import com.github.klyser8.earthbounds.entity.mob.RubroMaskType;
import com.github.klyser8.earthbounds.registry.*;
import com.github.klyser8.earthbounds.util.EarthUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RedstoneFossilBlock extends RedstoneOreBlock {

    protected long lastSoundTime = 0;
    private final Random random;

    public RedstoneFossilBlock(Settings settings) {
        super(settings);
        this.random = new Random();
    }

    @Override
    protected void dropExperience(ServerWorld world, BlockPos pos, int size) {}

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        /*if (EnchantmentHelper.getLevel(EarthboundEnchantments.CRUMBLE, stack) >= 5 || player.getAbilities().creativeMode) {
            boolean goldSkull = state.getBlock().equals(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK) ||
                    state.getBlock().equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
            boolean deepslate = state.getBlock().equals(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK) ||
                    state.getBlock().equals(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK);
            RubroEntity rubro = EarthboundEntities.RUBRO.create(world);
            if (rubro == null) return;
            rubro.refreshPositionAndAngles((double)pos.getX() + 0.5D, pos.getY(),
                    (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(rubro);
            rubro.initializeFossil(deepslate, goldSkull, -440 - world.random.nextInt(200), player);
            world.playSound(null, pos, EarthboundSounds.RUBRO_EAT,
                    SoundCategory.NEUTRAL, 0.5f, 1.4f + random.nextFloat() / 5);
        }*/
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        ItemStack stack = player.getMainHandStack();
        if (EnchantmentHelper.getLevel(EarthboundEnchantments.CRUMBLE, stack) >= EarthboundEnchantments.CRUMBLE.getMaxLevel() || player.getAbilities().creativeMode) {
            RubroMaskType maskType = RubroMaskType.getFromFossilBlock(state);
            RubroEntity rubro = EarthboundEntities.RUBRO.create(world);
            if (rubro == null) return;
            rubro.refreshPositionAndAngles((double)pos.getX() + 0.5D, pos.getY(),
                    (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.spawnEntity(rubro);
            rubro.initializeFossil(isFossilOfDeepslate(state), maskType, -440 - world.random.nextInt(200), player);
            world.playSound(null, pos, EarthboundSounds.ENTITY_EAT_REDSTONE,
                    SoundCategory.NEUTRAL, 0.5f, 1.4f + random.nextFloat() / 5);
        }
        if (player instanceof ServerPlayerEntity serverPlayer) {
            EarthboundsAdvancementCriteria.BREAK_REDSTONE_FOSSIL.trigger(serverPlayer, world.getBlockState(pos));
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        if (random.nextFloat() < 0.25f) {
            if (!EarthUtil.isOnCooldown(world.getTime(), lastSoundTime, 20)) {
                lastSoundTime = world.getTime();
                world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, EarthboundSounds.REDSTONE_FOSSIL_CREAK,
                        SoundCategory.NEUTRAL, 0.5f, 1.4f + random.nextFloat() / 5, true);
                playCrackleParticles(pos, world);
            }
        }
    }

    private void playCrackleParticles(BlockPos pos, World world) {
        int amount = random.nextInt(3) + 2;
        for (int i = 0; i < amount; i++) {
            for (Direction direction : Direction.values()) {
                BlockPos blockPos = pos.offset(direction);
                if (world.getBlockState(blockPos).isOpaqueFullCube(world, blockPos)) continue;
                Direction.Axis axis = direction.getAxis();
                double e = axis == Direction.Axis.X ? 0.5 + 0.5625 *
                        (double) direction.getOffsetX() : (double) random.nextFloat();
                double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 *
                        (double) direction.getOffsetY() : (double) random.nextFloat();
                double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 *
                        (double) direction.getOffsetZ() : (double) random.nextFloat();
                world.addParticle(EarthboundParticles.REDSTONE_CRACKLE,
                        (double) pos.getX() + e,
                        (double) pos.getY() + f,
                        (double) pos.getZ() + g,
                        0.0, 0.0, 0.0);
            }
        }
    }

    public static boolean isFossilOfDeepslate(BlockState state) {
        return state.isOf(EarthboundBlocks.DEEPSLATE_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CHARRED_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_VERDANT_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CRIMSON_REDSTONE_FOSSIL_BLOCK);
    }

}

package com.github.klyser8.earthbounds.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class PlayerBlockBreakEventHandler {

    private static final Map<PlayerEntity, Long> redstoneBreakTimes = new HashMap<>();

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register((PlayerBlockBreakEventHandler::handle));
    }

    private static void handle(World world, PlayerEntity player, BlockPos pos,
                               BlockState state, BlockEntity blockEntity) {
        if (state.getBlock() instanceof RedstoneOreBlock) {
            redstoneBreakTimes.put(player, world.getTime());
        }
    }

    /**
     * Used to store the world time where a player has last broken a redstone ore block.
     * Rubros use it for their target selection
     */
    public static Map<PlayerEntity, Long> getRedstoneBreakTimes() {
        return redstoneBreakTimes;
    }
}

package com.github.klyser8.earthbounds.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Represents a {@link BlockPos} which also stores block positions of adjacent blocks.
 * Can be used to check a
 */
public class AdvancedBlockPos {

    private BlockPos pos;
    private BlockPos north, east, south, west, up, down;

    public AdvancedBlockPos(BlockPos pos) {
        this.pos = pos;
        north = pos.mutableCopy().north();
        east = pos.mutableCopy().east();
        south = pos.mutableCopy().south();
        west = pos.mutableCopy().west();
        up = pos.mutableCopy().up();
        down = pos.mutableCopy().down();
    }

    /**
     * Returns all the adjacent block positions to the main block (as a list).
     */
    public List<BlockPos> getAllFaces() {
        return List.of(north, east, south, west, up, down);
    }

    public BlockPos getPos() {
        return pos;
    }


    public BlockPos north() {
        return north;
    }

    public BlockPos east() {
        return east;
    }

    public BlockPos south() {
        return south;
    }

    public BlockPos west() {
        return west;
    }

    public BlockPos up() {
        return up;
    }

    public BlockPos down() {
        return down;
    }
}

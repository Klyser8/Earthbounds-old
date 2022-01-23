package com.github.klyser8.earthbounds.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Represents a {@link BlockPos} which also stores block positions of adjacent blocks.
 * Can be used to check a
 */
public class AdvancedBlockPos {

    private final BlockPos pos;
    private final BlockPos north, east, south, west, up, down;

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
     * Returns all the adjacent block positions to the main block (as an array).
     */
    public BlockPos[] getAllFaces() {
        return new BlockPos[]{north, east, south, west, up, down};
    }

    public BlockPos getPos() {
        return new BlockPos(pos);
    }


    public BlockPos north() {
        return new BlockPos(north);
    }

    public BlockPos east() {
        return new BlockPos(east);
    }

    public BlockPos south() {
        return new BlockPos(south);
    }

    public BlockPos west() {
        return new BlockPos(west);
    }

    public BlockPos up() {
        return new BlockPos(up);
    }

    public BlockPos down() {
        return new BlockPos(down);
    }
}

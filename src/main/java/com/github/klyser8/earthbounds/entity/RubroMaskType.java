package com.github.klyser8.earthbounds.entity;

import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import net.minecraft.block.BlockState;

public enum RubroMaskType {

    DEFAULT(0),
    GILDED(1),
    CRYSTALLINE(2),
    CHARRED(3),
    VERDANT(4),
    CRIMSON(5);

    private final int id;

    RubroMaskType(int id) {
        this.id = id;
    }

    public static RubroMaskType getFromId(int id) {
        return switch (id) {
            case 1 -> GILDED;
            case 2 -> CRYSTALLINE;
            case 3 -> CHARRED;
            case 4 -> VERDANT;
            case 5 -> CRIMSON;
            default -> DEFAULT;
        };
    }

    public static RubroMaskType getFromFossilBlock(BlockState state) {
        if (state.isOf(EarthboundBlocks.GILDED_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_GILDED_REDSTONE_FOSSIL_BLOCK)) {
            return GILDED;
        } else if (state.isOf(EarthboundBlocks.CRYSTALLINE_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CRYSTALLINE_REDSTONE_FOSSIL_BLOCK)) {
            return CRYSTALLINE;
        } else if (state.isOf(EarthboundBlocks.CHARRED_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CHARRED_REDSTONE_FOSSIL_BLOCK)) {
            return CHARRED;
        } else if (state.isOf(EarthboundBlocks.VERDANT_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_VERDANT_REDSTONE_FOSSIL_BLOCK)) {
            return VERDANT;
        } else if (state.isOf(EarthboundBlocks.CRIMSON_REDSTONE_FOSSIL_BLOCK)
                || state.isOf(EarthboundBlocks.DEEPSLATE_CRIMSON_REDSTONE_FOSSIL_BLOCK)) {
            return CRIMSON;
        } else {
            return DEFAULT;
        }
    }

    public int getId() {
        return id;
    }
}

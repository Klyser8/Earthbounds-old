package com.github.klyser8.earthbounds.entity;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * A conductive entity is one which may have a varying temperature based on its environment and state.
 */
public interface Conductive {

    float MAX_HEAT = 400.0f; //The maximum possible temperature a Conductive entity may reach.
    int PERIOD = 10; //How often the below methods should be run. Affects the methods returns.

    /**
     * Calculates how much the heat of the entity should vary in its current environment and based on its state.
     * Looks at the following:
     * - Biome temperature
     * - Current dimension
     * - If the entity is standing in fire or on a magma block
     * - If the entity is in lava
     * - If the entity is touching water
     * - If the entity is inside powdered snow
     *
     * @param world the world the entity is in
     * @param origin the entity's position
     * @return the number the heat should change by.
     */
    static float calculateHeatChangePerPeriod(World world, Vec3d origin, boolean ignoreEnvironment) {
        float change = 0;
        BlockPos blockOrigin = new BlockPos(origin);
        if (!ignoreEnvironment) {
            Biome biome = world.getBiomeAccess().getBiome(blockOrigin);
            if (biome == null) {
                change -= 0.5f;
            } else {
                change += (biome.getTemperature() - 1.2) / 3.0f; //Lowest temperature is 0.15 (0.0015), highest is 2 (0.02).
                if (biome.getCategory() == Biome.Category.NETHER) {
                    change += 0.75;
                }
            }
        }
        if (world.getBlockState(blockOrigin).getBlock().equals(Blocks.LAVA)) {
            change+= 2.0f;
        }
        if (world.isWater(blockOrigin)) {
            change += -5.0f;
        }
        BlockPos[] posArray = new BlockPos[]{
                new BlockPos(origin),
                new BlockPos(origin).up(),
                new BlockPos(origin).down(),
                new BlockPos(origin).north(),
                new BlockPos(origin).south(),
                new BlockPos(origin).east(),
                new BlockPos(origin).west(),
                new BlockPos(origin).north().west(),
                new BlockPos(origin).north().east(),
                new BlockPos(origin).south().west(),
                new BlockPos(origin).south().east(),
                new BlockPos(origin).up().north(),
                new BlockPos(origin).up().south(),
                new BlockPos(origin).up().east(),
                new BlockPos(origin).up().west(),
                new BlockPos(origin).up().north().west(),
                new BlockPos(origin).up().north().east(),
                new BlockPos(origin).up().south().west(),
                new BlockPos(origin).up().south().east(),
                new BlockPos(origin).down().north(),
                new BlockPos(origin).down().south(),
                new BlockPos(origin).down().east(),
                new BlockPos(origin).down().west(),
                new BlockPos(origin).down().north().west(),
                new BlockPos(origin).down().north().east(),
                new BlockPos(origin).down().south().west(),
                new BlockPos(origin).down().south().east(),
        };
        for (BlockPos pos : posArray) {
            Block block = world.getBlockState(pos).getBlock();
            float blockModifier = 0.0f;
            if (block.equals(Blocks.ICE) || block.equals(Blocks.BLUE_ICE) ||
                    block.equals(Blocks.PACKED_ICE) || block.equals(Blocks.FROSTED_ICE)) {
                blockModifier = -0.09f;
            } else if (block.equals(Blocks.POWDER_SNOW) || block.equals(Blocks.POWDER_SNOW_CAULDRON)) {
                blockModifier = -0.16f;
            } else if (block.equals(Blocks.TORCH) || block.equals(Blocks.WALL_TORCH)) {
                blockModifier = 0.04f;
            } else if (block.equals(Blocks.MAGMA_BLOCK) ||
                    block.equals(Blocks.SOUL_TORCH) || block.equals(Blocks.SOUL_WALL_TORCH) ||
                    block instanceof AbstractFurnaceBlock) {
                blockModifier = 0.08f;
            } else if (block.equals(Blocks.FIRE) || block.equals(Blocks.CAMPFIRE)) {
                blockModifier = 0.12f;
            } else if (block.equals(Blocks.LAVA) || block.equals(Blocks.LAVA_CAULDRON)) {
                blockModifier = 0.16f;
            } else if (block.equals(Blocks.SOUL_FIRE) || block.equals(Blocks.SOUL_CAMPFIRE)) {
                blockModifier = 0.20f;
            }

            if (pos.equals(blockOrigin) || pos.equals(blockOrigin.mutableCopy().down())) {
                blockModifier *= 3.0f;
            }
            change += blockModifier;
        }
        change *= PERIOD;
        return change;
    }
    /*static float calculateHeatChangePerTick(World world, BlockPos origin, boolean isInLava, boolean isTouchingWater) {
        Biome biome = world.getBiomeAccess().getBiome(origin);
        if (biome == null) {
            return -0.005f;
        }
        float formattedTemp = biome.getTemperature() / 200; //Lowest temperature is 0.15 (0.0015), highest is 2 (0.02).
        float incandescenceChange = (-0.0125f + formattedTemp);
        Block block = world.getBlockState(origin).getBlock();
        if (biome.getCategory() == Biome.Category.NETHER) {
            incandescenceChange += 0.015;
        }
        if (block == Blocks.FIRE || world.getBlockState(origin.down()).getBlock() == Blocks.MAGMA_BLOCK) {
            incandescenceChange += 0.0125f;
        }
        if (isInLava) {
            incandescenceChange+= 0.020f;
        }
        if (isTouchingWater) {
            incandescenceChange += -0.05f;
        }
        if (block == Blocks.POWDER_SNOW) {
            incandescenceChange += -0.25f;
        }

//        System.out.println(incandescenceChange);
        return incandescenceChange;
    }*/

    /**
     * Logic to update the entity's heat.
     * @param period how often the heat should be updated
     *
     * @return how much the entity's heat has changed
     */
    float updateHeat(int period);

    /**
     * Should return the entity's current heat.
     * @return the entity's current heat.
     */
    float getCurrentHeat();

    /**
     * The heat to set the entity to
     */
    void setCurrentHeat(float heat);

}

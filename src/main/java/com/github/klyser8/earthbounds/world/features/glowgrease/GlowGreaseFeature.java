package com.github.klyser8.earthbounds.world.features.glowgrease;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.util.EarthUtil;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.GlowLichenFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GlowGreaseFeature extends Feature<GlowLichenFeatureConfig> {

    public GlowGreaseFeature(Codec<GlowLichenFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<GlowLichenFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos.Mutable origin = context.getOrigin().mutableCopy();
        BlockState ogState = world.getBlockState(origin);
        Random random = context.getRandom();
        GlowLichenFeatureConfig config = context.getConfig();
        //If the location is air, then it's good!
        if (!ogState.isAir()) {
            return false;
        }
        boolean placed = false;
        GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
        for (int i = 0; i < 100; i++) {
            if (i > 0) {
                //Offset origin towards block which state is AIR
                List<BlockPos> posList = EarthUtil.getAdjacentPos(origin);
                Collections.shuffle(posList);
                for (BlockPos pos : posList) {
                    if (world.getBlockState(pos).isAir()) {
                        origin.set(pos);
                    }
                }
            }
            //Check all adjacent blocks, to see where the grease can be placed.
            for (Direction dir : Direction.values()) {
                //If the grease cant be placed on this block, then look for another direction
                BlockPos supportingPos = origin.offset(dir, 1);
                if (!config.canPlaceOn.contains(world.getBlockState(supportingPos).getBlock())) {
                    continue;
                }
                BlockState currentState;
                //If the state at position is already a glow grease, then just apply it to other sides.
                if (world.getBlockState(origin).getBlock() instanceof GlowGreaseSplatBlock) {
                    //Add a randomness to grease placement
                    if (random.nextBoolean()) {
                        currentState = world.getBlockState(origin);     //Current state: the state of the block at origin
                    } else {
                        currentState = null;
                    }
                } else {
                    currentState = glowGreaseBlock.getDefaultState();
                }
                //grease state: the updated state of the block at origin, with directions applied.
                if (currentState == null) {
                    break;
                }
                BlockState greaseState = glowGreaseBlock.withDirection(currentState, world, origin, dir);
                if (greaseState == null) {
                    continue;
                }
                //If the block can't be set, avoid placing anything else to avoid errors
                if (!world.isValidForSetBlock(origin)) {
                    return false;
                }
                world.setBlockState(origin, greaseState, Block.NOTIFY_ALL);
                world.getChunk(origin).markBlockForPostProcessing(origin);
                origin.set(origin);
                if (!placed) {
                    placed = true;
                }
            }
        }
        return placed;
    }

    /*
    @Override
    public boolean generate(FeatureContext<GlowLichenFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos.Mutable originMutable = context.getOrigin().mutableCopy();
        BlockState ogState = world.getBlockState(originMutable);
        Random random = context.getRandom();
        GlowLichenFeatureConfig config = context.getConfig();
        //If the location is air, then it's good!
        if (!ogState.isAir()) {
            return false;
        }
        boolean placed = false;
        GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
        //Check all adjacent blocks, to see where the grease can be placed.
        for (Direction dir : Direction.values()) {
            //If the grease cant be placed on this block, then look for another direction
            BlockPos supportingPos = originMutable.offset(dir, 1);
            if (!config.canPlaceOn.contains(world.getBlockState(supportingPos).getBlock())) {
                continue;
            }
            BlockState currentState;
            //If the state at position is already a glow grease, then just apply it to other sides.

            if (world.getBlockState(originMutable).getBlock() instanceof GlowGreaseSplatBlock) {
                currentState = world.getBlockState(originMutable);     //Current state: the state of the block at origin
            } else {
                currentState = glowGreaseBlock.getDefaultState();
            }
            //grease state: the updated state of the block at origin, with directions applied.
            BlockState greaseState = glowGreaseBlock.withDirection(currentState, world, originMutable, dir);
            if (greaseState == null) {
                continue;
            }
            world.setBlockState(originMutable, greaseState, Block.NOTIFY_ALL);
            world.getChunk(originMutable).markBlockForPostProcessing(originMutable);
            originMutable.set(originMutable);
            if (!placed) {
                placed = true;
            }
        }
        return placed;
    }
     */

    /*
    @Override
    public boolean generate(FeatureContext<GlowLichenFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        BlockState originState = world.getBlockState(origin);
        Random random = context.getRandom();
        GlowLichenFeatureConfig config = context.getConfig();
        if (!originState.isAir()) {
            return false;
        }
        List<Direction> list = GlowGreaseFeature.shuffleDirections(config, random);
        if (GlowGreaseFeature.generate(world, origin, world.getBlockState(origin), config, random, list)) {
            return true;
        }
        int attempts = random.nextInt(5) + 5;
        BlockPos.Mutable mutable = origin.mutableCopy();
        boolean placed = false;
        for (int a = 0; a < attempts; a++) {
            loop0: for (Direction direction : list) {
                List<Direction> directions = GlowGreaseFeature.shuffleDirections(config, random, direction.getOpposite());
                for (int i = 0; i < config.searchRange; ++i) {
                    mutable.set(origin, direction);
                    BlockState blockState = world.getBlockState(mutable);
                    if (!blockState.isAir() && !blockState.isOf(EarthboundBlocks.GLOW_GREASE_SPLAT)) continue loop0;
                    if (!GlowGreaseFeature.generate(world, mutable, blockState, config, random, directions)) continue;
                    placed = true;
                    break;
                }
            }
        }
        return placed;
    }
     */

    public static boolean generate(StructureWorldAccess world, BlockPos pos, BlockState state,
                                   GlowLichenFeatureConfig config, Random random, List<Direction> directions) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        int spreadAmount = -1;
        boolean hasSpread = false;
        while (/*random.nextFloat() < config.spreadChance && */spreadAmount++ < 10) {
            Direction direction = Direction.random(random);
            BlockState blockState = world.getBlockState(mutable.set(pos, direction));
            if (!config.canPlaceOn.contains(blockState.getBlock())) continue;
            GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
            BlockState greaseState = glowGreaseBlock.withDirection(state, world, pos, direction);
            if (greaseState == null) {
                continue;
            }
            world.setBlockState(pos, greaseState, Block.NOTIFY_ALL);
            world.getChunk(pos).markBlockForPostProcessing(pos);
            if (random.nextFloat() < config.spreadChance) {
                glowGreaseBlock.trySpreadRandomly(greaseState, world, pos, direction, random, true);
            }
            if (!hasSpread) {
                hasSpread = true;
            }
            mutable = mutable.offset(direction, 1).mutableCopy();
        }
        return hasSpread;
    }

    /*
    public static boolean generate(StructureWorldAccess world, BlockPos pos, BlockState state,
                                   GlowLichenFeatureConfig config, Random random, List<Direction> directions) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (Direction direction : directions) {
            BlockState blockState = world.getBlockState(mutable.set(pos, direction));
            if (!config.canPlaceOn.contains(blockState.getBlock())) continue;
            GlowGreaseSplatBlock glowGreaseBlock = EarthboundBlocks.GLOW_GREASE_SPLAT;
            BlockState greaseState = glowGreaseBlock.withDirection(state, world, pos, direction);
            if (greaseState == null) {
                return false;
            }
            world.setBlockState(pos, greaseState, Block.NOTIFY_ALL);
            world.getChunk(pos).markBlockForPostProcessing(pos);
            if (random.nextFloat() < config.spreadChance) {
                glowGreaseBlock.trySpreadRandomly(greaseState, world, pos, direction, random, true);
            }
            return true;
        }
        return false;
    }
     */

    public static List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random) {
        ArrayList<Direction> list = Lists.newArrayList(config.directions);
        Collections.shuffle(list, random);
        return list;
    }

    public static List<Direction> shuffleDirections(GlowLichenFeatureConfig config, Random random, Direction excluded) {
        List<Direction> list = config.directions.stream().filter(direction -> direction != excluded).collect(Collectors.toList());
        Collections.shuffle(list, random);
        return list;
    }
}

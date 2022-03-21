package com.github.klyser8.earthbounds.world.features.glowgrease;

import com.github.klyser8.earthbounds.block.GlowGreaseSplatBlock;
import com.github.klyser8.earthbounds.registry.EarthboundBlocks;
import com.github.klyser8.earthbounds.util.EarthUtil;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Collections;
import java.util.List;
import java.util.Random;

//TODO replace glow lichen feature config with its own feature config
public class GlowGreaseFeature extends Feature<DefaultFeatureConfig> {

    private final RegistryEntryList<Block> placeableList = RegistryEntryList.of(Block::getRegistryEntry, List.of(
            Blocks.STONE,
            Blocks.ANDESITE,
            Blocks.DIORITE,
            Blocks.GRANITE,
            Blocks.DRIPSTONE_BLOCK,
            Blocks.CALCITE,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.OAK_WOOD,
            Blocks.MOSS_BLOCK));

    public GlowGreaseFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos.Mutable origin = context.getOrigin().mutableCopy();
        BlockState ogState = world.getBlockState(origin);
        Random random = context.getRandom();
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
                if (!placeableList.contains(world.getBlockState(supportingPos).getBlock().getRegistryEntry())) {
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
}
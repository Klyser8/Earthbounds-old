package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

public class EarthboundItems {

    public static final Item DEBUG_ITEM = new Item(new FabricItemSettings().rarity(Rarity.EPIC)) {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            BlockPos origin = context.getBlockPos();
            World world = context.getWorld();
            int xLength = 12;
            int yDepth = -8;
            int zLength = 12;
            if (!world.isClient) {
                for (int y = -yDepth; y > yDepth; y--) {
                    int absY = Math.abs(y);
                    int modX = xLength - world.random.nextInt(3) - absY /*/ (Math.abs(y - 1))*/;
                    int modZ = zLength - world.random.nextInt(3) - absY/*/ (Math.abs(y - 1))*/;
                    for (int x = Math.round(-(modX / 2.0f)) + world.random.nextInt(6) - 2; x < Math.round(modX / 2.0f) + world.random.nextInt(6) - 2; x++) {
                        for (int z = Math.round(-(modZ / 2.0f)) + world.random.nextInt(6) - 2; z < Math.round(modZ / 2.0f) + world.random.nextInt(6) - 2; z++) {
                            BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                            BlockState state = world.getBlockState(origin);
                            if ((!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty() &&
                                    world.getBlockState(mutable).shouldSuffocate(world, mutable) &&
                                    world.getBlockState(mutable).isOpaque())) {
                                float chance = world.random.nextFloat();
                                if (chance > 0.97f && absY != Math.abs(yDepth) && absY != 0) {
                                    state = Blocks.COAL_BLOCK.getDefaultState();
                                } else if (chance > 0.60) {
                                    state = Blocks.TUFF.getDefaultState();
                                } else if (chance > 0.25) {
                                    state = Blocks.COAL_ORE.getDefaultState();
                                }
                                world.setBlockState(mutable, state, Block.NOTIFY_ALL);
                            }
                        }
                    }
                }
                createHole(world, origin, (int) (xLength / 1.5), -(yDepth + 2));
            }
            return super.useOnBlock(context);
        }

        private void createHole(World world, BlockPos origin, int radius, int height) {
            int j;
            int k;
            int l;
            int maxJ = 16;
            int maxK = 16;
            int maxL = 16;
            for (j = 0; j < maxJ; ++j) {
                for (k = 0; k < maxK; ++k) {
                    block2: for (l = 0; l < maxL; ++l) {
                        if ((j != 0 && j != maxJ - 1) && (k != 0 && k != maxK - 1) && (l != 0 && l != maxL - 1)) continue;
                        double d = (float)j / (maxJ - 1.0f) * 2.0f - 1.0f;
                        double e = (float)k / (maxK - 1.0f) * 2.0f - 1.0f;
                        double f = (float)l / (maxL - 1.0f) * 2.0f - 1.0f;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        double m = origin.getX();
                        double n = origin.getY();
                        double o = origin.getZ();
                        for (float h = 2 * (0.7f + world.random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                            BlockPos blockPos = new BlockPos(m, n, o);
                            if (!world.isInBuildLimit(blockPos)) continue block2;
                            if (world.random.nextBoolean()) {
                                h -= world.random.nextFloat() / 2;
                            }
                            m += d * (double)0.3f;
                            n += e * (double)0.3f;
                            o += f * (double)0.3f;
                            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                        }
                    }
                }
            }

            /*world.setBlockState(origin, Blocks.AIR.getDefaultState());
            for (int y = -(radius / 2 + random.nextInt(2)); y < radius / 2 + random.nextInt(2); y++) {
                for (int x = height / 2 + random.nextInt(2); x > -(height / 2 + random.nextInt(2)); x--) {
                    for (int z = height / 2 + random.nextInt(2); z > -(height / 2 + random.nextInt(2)); z--) {
                        BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                        boolean isConnected =
                                *//*y <= 0 && *//*world.getBlockState(
                                    mutable.mutableCopy().add(0, -1, 0)).getBlock() != Blocks.AIR *//*||
                                y > 1 && world.getBlockState(
                                        mutable.mutableCopy().add(0, 1, 0)).getBlock() != Blocks.AIR*//*;
                        if (!isConnected ||
                                (Math.abs(y) / (((float) height) / 2f) < random.nextFloat() &&
                                Math.abs(x) / (((float) radius) / 2f) < random.nextFloat() &&
                                Math.abs(z) / (((float) radius) / 2f) < random.nextFloat()) &&
                                        (!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty() &&
                                        world.getBlockState(mutable).shouldSuffocate(world, mutable) &&
                                        world.getBlockState(mutable).isOpaque())) {
                            world.setBlockState(mutable, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }*/
        }
    };
    public static final Item CARBORANEA_SPAWN_EGG = new SpawnEggItem(EarthboundEntities.CARBORANEA, 4671303,
            13913600, new Item.Settings().group(ItemGroup.MISC));
    public static final Item CARBORANEA_BUCKET = new EntityBucketItem(EarthboundEntities.CARBORANEA,
            Fluids.LAVA, EarthboundSounds.CARBORANEA_BUCKET_EMPTY, new Item.Settings().maxCount(1).group(ItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "debug_item"), DEBUG_ITEM);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_spawn_egg"), CARBORANEA_SPAWN_EGG);
        Registry.register(Registry.ITEM,
                new Identifier(Earthbounds.MOD_ID, "carboranea_bucket"), CARBORANEA_BUCKET);
    }
}

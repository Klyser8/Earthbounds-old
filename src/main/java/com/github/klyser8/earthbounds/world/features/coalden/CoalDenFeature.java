package com.github.klyser8.earthbounds.world.features.coalden;

import com.github.klyser8.earthbounds.entity.mob.CarboraneaEntity;
import com.github.klyser8.earthbounds.registry.EarthboundEntities;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.Random;

public class CoalDenFeature extends Feature<CoalDenFeatureConfig> {

    private final Random random;

    public CoalDenFeature(Codec<CoalDenFeatureConfig> codec) {
        super(codec);
        this.random = new Random();
    }

    @Override
    public boolean generate(FeatureContext<CoalDenFeatureConfig> context) {
        BlockPos origin = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        int xLength = 12;
        int yDepth = -8;
        int zLength = 12;
        for (int y = -yDepth; y > yDepth; y--) {
            int absY = Math.abs(y);
            int modX = xLength - random.nextInt(3) - absY /*/ (Math.abs(y - 1))*/;
            int modZ = zLength - random.nextInt(3) - absY/*/ (Math.abs(y - 1))*/;
            for (int x = Math.round(-(modX / 2.0f)) + random.nextInt(6) - 2; x < Math.round(modX / 2.0f) + random.nextInt(6) - 2; x++) {
                for (int z = Math.round(-(modZ / 2.0f)) + random.nextInt(6) - 2; z < Math.round(modZ / 2.0f) + random.nextInt(6) - 2; z++) {
                    BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                    BlockState state = world.getBlockState(origin);
                    if ((!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty() &&
                            world.getBlockState(mutable).shouldSuffocate(world, mutable) &&
                            world.getBlockState(mutable).isOpaque())) {
                        float chance = random.nextFloat();
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
            createHole(world, origin, (int) (xLength / 1.5), -(yDepth + 2));
        }
        spawnCarboranea(world, origin);
        return true;
    }

    //FIXME radius and height parameters do not change anything. Code stolen from Explosion.java
    private void createHole(StructureWorldAccess world, BlockPos origin, int radius, int height) {
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
                    for (float h = 2 * (0.7f + random.nextFloat() * 0.6f); h > 0.0f; h -= 0.22500001f) {
                        BlockPos blockPos = new BlockPos(m, n, o);
                        if (world.isOutOfHeightLimit(blockPos)) continue block2;
                        if (random.nextBoolean()) {
                            h -= random.nextFloat() / 2;
                        }
                        m += d * (double)0.3f;
                        n += e * (double)0.3f;
                        o += f * (double)0.3f;
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.FORCE_STATE);
                    }
                }
            }
        }
        /*world.setBlockState(origin, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        for (int y = -(radius / 2 + random.nextInt(2)); y < radius / 2 + random.nextInt(2); y++) {
            for (int x = height / 2 + random.nextInt(2); x > -(height / 2 + random.nextInt(2)); x--) {
                for (int z = height / 2 + random.nextInt(2); z > -(height / 2 + random.nextInt(2)); z--) {
                    BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                    if (world.getBlockState(mutable.mutableCopy().add(0, -1, 0)).getBlock() == Blocks.AIR ||
                            (Math.abs(y) / (((float) height) / 2f) < random.nextFloat() &&
                                    Math.abs(x) / (((float) radius) / 2f) < random.nextFloat() &&
                                    Math.abs(z) / (((float) radius) / 2f) < random.nextFloat()) &&
                                    (!world.getBlockState(mutable).getCollisionShape(world, mutable).isEmpty() &&
                                            world.getBlockState(mutable).shouldSuffocate(world, mutable) &&
                                            world.getBlockState(mutable).isOpaque())) {
                        world.setBlockState(mutable, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    }
                }
            }
        }*/
    }

    private void spawnCarboranea(StructureWorldAccess world, BlockPos spawnPoint) {
        CarboraneaEntity carboranea = EarthboundEntities.CARBORANEA.create(world.toServerWorld());
        if (carboranea != null) {
            carboranea.setPersistent();
            carboranea.refreshPositionAndAngles((double) spawnPoint.getX() + 0.5, spawnPoint.getY(),
                    (double) spawnPoint.getZ() + 0.5, 0.0f, 0.0f);
            carboranea.initialize(world, world.getLocalDifficulty(spawnPoint),
                    SpawnReason.CHUNK_GENERATION, null, null);
            world.spawnEntityAndPassengers(carboranea);
        }
    }
}

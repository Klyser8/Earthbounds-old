package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.Earthbounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import java.util.Random;

public class EarthboundItems {

    public static final Item DEBUG_ITEM = new Item(new FabricItemSettings().group(ItemGroup.MISC)) {
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
            Random random = world.random;
            world.setBlockState(origin, Blocks.AIR.getDefaultState());
            for (int y = -(radius / 2 + random.nextInt(2)); y < radius / 2 + random.nextInt(2); y++) {
                for (int x = height / 2 + random.nextInt(2); x > -(height / 2 + random.nextInt(2)); x--) {
                    for (int z = height / 2 + random.nextInt(2); z > -(height / 2 + random.nextInt(2)); z--) {
                        BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                        boolean isConnected =
                                /*y <= 0 && */world.getBlockState(
                                    mutable.mutableCopy().add(0, -1, 0)).getBlock() != Blocks.AIR /*||
                                y > 1 && world.getBlockState(
                                        mutable.mutableCopy().add(0, 1, 0)).getBlock() != Blocks.AIR*/;
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
            }/*for (int y = -(radius / 2 + random.nextInt(2)); y < radius / 2; y++) {
                for (int x = depth / 2 + random.nextInt(2); x > -(depth / 2); x--) {
                    for (int z = depth / 2 + random.nextInt(2); z > -(depth / 2); z--) {
                        if (Math.abs(y) / (((float) depth) / 2f) < random.nextFloat() &&
                                Math.abs(x) / (((float) radius) / 2f) < random.nextFloat() &&
                                Math.abs(z) / (((float) radius) / 2f) < random.nextFloat()) {
                            BlockPos.Mutable mutable = origin.mutableCopy().add(x, y, z).mutableCopy();
                            System.out.println(mutable);
                            world.setBlockState(mutable, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }*/
        }
    };

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Earthbounds.MOD_ID, "debug_item"), DEBUG_ITEM);
    }
}

package com.github.klyser8.earthbounds.dispenser;

import com.github.klyser8.earthbounds.item.AmethystDust;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AmethystDustDispenserBehavior implements DispenserBehavior {

    @Override
    public ItemStack dispense(BlockPointer pointer, ItemStack stack) {
        ServerWorld world = pointer.getWorld();
        Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
        if (stack.getItem() instanceof AmethystDust dust) {
            dust.trigger(world, Vec3d.ofCenter(pointer.getPos()).add(Vec3d.of(direction.getVector()).multiply(0.6)),
                    new Vec3d(direction.getOffsetX(), direction.getOffsetY() + 0.2, direction.getOffsetZ()),
                    true);
        }
        stack.decrement(1);
        return stack;
    }

}

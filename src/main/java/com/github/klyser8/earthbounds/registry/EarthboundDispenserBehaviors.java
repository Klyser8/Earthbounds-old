package com.github.klyser8.earthbounds.registry;

import com.github.klyser8.earthbounds.dispenser.AmethystDustDispenserBehavior;
import com.github.klyser8.earthbounds.item.AmethystDust;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class EarthboundDispenserBehaviors {

    public static final DispenserBehavior SPREAD_AMETHYST_DUST = new AmethystDustDispenserBehavior();

    // get custom dispenser behavior
    // this checks conditions such as the item and certain block or entity being in front of the dispenser to decide which rule to return
    // if the conditions for the rule match, it returns the instance of the dispenser behavior
    // returns null to fallback to vanilla (or another mod's) behavior for the given item
    public static DispenserBehavior getCustomDispenserBehavior(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof AmethystDust) {
            return SPREAD_AMETHYST_DUST;
        }
        return null;
    }

}

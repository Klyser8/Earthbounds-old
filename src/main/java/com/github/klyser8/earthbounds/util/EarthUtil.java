package com.github.klyser8.earthbounds.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EarthUtil {

    public static final List<Vec3i> blockConnections = createConnections();

    /**
     * Checks whether the entity has blocked the most recent instance of damage or not.
     * @deprecated should simply use {@link LivingEntity#blockedByShield(DamageSource)}
     */
    @Deprecated
    public static boolean hasBlockedMostRecentDamage(LivingEntity entity) {
        return entity.getDamageTracker().getMostRecentDamage() != null
                && entity.getDamageTracker().getMostRecentDamage().getDamageSource() != null
                && entity.blockedByShield(entity.getDamageTracker().getMostRecentDamage().getDamageSource());
    }

    /**
     * Checks whether the player has the specified item stack in their hotbar.
     *
     * @param player the player to be looked into
     * @param stack the stack to look for
     * @return if the player's hotbar contains the stack
     */
    public static boolean playerHotbarContains(PlayerEntity player, ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            ItemStack hotbarStack = player.getInventory().getStack(i);
            if (hotbarStack.isEmpty() || !stack.isItemEqualIgnoreDamage(hotbarStack)) continue;
            return true;
        }
        return false;
    }

    /**
     * Checks whether the player has one of the stacks present
     * in the set in their hotbar.
     *
     * @param player the player to be looked into
     * @param stacks the stacks to look for
     * @return if the player's hotbar contains one of the stacks
     */
    public static boolean playerHotbarContains(PlayerEntity player, Set<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            for (int i = 0; i < 9; i++) {
                ItemStack hotbarStack = player.getInventory().getStack(i);
                if (hotbarStack.isEmpty() || !stack.isItemEqualIgnoreDamage(hotbarStack)) continue;
                return true;
            }
        }
        return false;
    }

    /**
     * Util method used to check if something is on cooldown.
     * It subtracts the time the thing was executed from the current world time,
     * then it checks if the result is higher than the cooldown.
     *
     * @param currentTime the current world time
     * @param timeOfUse the time the task was executed
     * @param cooldown the length of the cooldown
     * @return true if the resulting number is below the cooldown, false otherwise.
     */
    public static boolean isOnCooldown(long currentTime, long timeOfUse, int cooldown) {
        return currentTime - timeOfUse < cooldown;
    }

    /**
     * Whether an entity is looking at another entity's head.
     *
     * @param looker the entity which is looking
     * @param lookedAt the entity being looked at
     * @return true if the looker is looking at the lookedAt
     */
    public static boolean isEntityLookingAtEntity(LivingEntity looker, LivingEntity lookedAt) {
        Vec3d lookerRotation = looker.getRotationVec(1.0f).normalize();
        lookerRotation = new Vec3d(lookerRotation.x, lookerRotation.y, lookerRotation.z);
        Vec3d vec = new Vec3d(
                lookedAt.getX() - looker.getX(),
                lookedAt.getEyeY() - looker.getEyeY(),
                lookedAt.getZ() - looker.getZ());
        double length = vec.length();
        double dot = lookerRotation.dotProduct(vec.normalize());
        if (dot > 1.0 - 0.050 / length) {
            /*System.out.println(looker.canSee(lookedAt));*/
            return looker.canSee(lookedAt);
        }
        return false;
    }

    /**
     * Calculates the jump height of an entity given the jump velocity.
     *
     * @param jumpVelocity the entity's jump velocity
     * @return the jump height in blocks.
     */
    public static float calculateJumpHeight(float jumpVelocity) {
        return jumpVelocity * 2.25f;
    }


    /**
     * Source: lilypuree#0239 (Discord)
     *
     * Searches X amount of times for a block with a state fulfilling the given predicate.
     * The search will be outward starting from the root position.
     *
     *
     *
     * @param rootPos the start position of the search
     * @param predicate the condition the blockstate must succeed
     * @return the BlockPos fulfilling the predicate given, or null if the search fails.
     */
    @Deprecated
    public static BlockPos search(BlockPos rootPos, int diameter, Predicate<BlockPos> predicate) {
        Set<Long> visited = new LongOpenHashSet();
        LinkedList<BlockPos> queue = new LinkedList<>();
        visited.add(rootPos.asLong());
        queue.add(rootPos);
        int attempt = 0;
        while (queue.size() != 0 && Math.pow((2 * diameter + 1), 3) - 1 != attempt) {
            BlockPos currentBlockPos = queue.poll();
            if (predicate.test(currentBlockPos)) {
                return currentBlockPos;
            }
            for (Vec3i v : blockConnections) {
                BlockPos nextBlockPos = currentBlockPos.add(v);
                if (!visited.contains(nextBlockPos.asLong())) {
                    visited.add(nextBlockPos.asLong());
                    queue.add(nextBlockPos);
                }
            }
            attempt++;
        }
        return null;
    }

    /**
     * Creates a list containing all adjacent positions to the given position.
     *
     * @param pos the origin position
     * @return the list with the adjacent block positions
     */
    public static List<BlockPos> getAdjacentPos(BlockPos pos) {
        List<BlockPos> posList = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            posList.add(pos.offset(direction, 1));
        }
        return posList;
    }

    private static List<Vec3i> createConnections() {
        List<Vec3i> connections = new LinkedList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    connections.add(new Vec3i(x, y, z));
                }
            }
        }
        return connections;
    }

}
